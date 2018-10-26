package net.inveed.commons.utils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.inveed.commons.NumberedException;
import net.inveed.commons.INumberedException.IErrorCode;


public class CryptoUtil {
	private static final String SYSTEM_PREFIX = "CRPT";
	//2000-2999 - CryptoErrors
		public static final IErrorCode ERR_CRYPTO_NO_SUCH_ALGORYTHM = NumberedException.registerCode(SYSTEM_PREFIX, 2001, "No such algorythm: %s", 1);
		public static final IErrorCode ERR_CRYPTO_NO_SUCH_PADDING = NumberedException.registerCode(SYSTEM_PREFIX, 2002, "No such padding: %s", 1);
		public static final IErrorCode ERR_CRYPTO_BAD_PADDING = NumberedException.registerCode(SYSTEM_PREFIX, 2003, "Bad padding", 0);
		public static final IErrorCode ERR_CRYPTO_INVALID_KEY = NumberedException.registerCode(SYSTEM_PREFIX, 2004, "Invalid key", 0);
		public static final IErrorCode ERR_CRYPTO_ILLEGAL_BLOCK_SIZE = NumberedException.registerCode(SYSTEM_PREFIX, 2005, "Illegal block size", 0);
		//public static final ErrorCode ERR_CRYPTO_INVALID_KEY_SPEC = NumberedException.registerCode(ERRPREFIX, 2006, "Invalid key spec", 0);
		public static final IErrorCode ERR_CRYPTO_DATA_LENGTH_ERROR = NumberedException.registerCode(SYSTEM_PREFIX, 2007, "Invalid data length", 0);
		public static final IErrorCode ERR_CRYPTO_ILLEGAL_STATE = NumberedException.registerCode(SYSTEM_PREFIX, 2008, "Illegal state", 0);
		public static final IErrorCode ERR_CRYPTO_INVALID_CYPHER_TEXT = NumberedException.registerCode(SYSTEM_PREFIX, 2009, "Invalid cypher text", 0);
		public static final IErrorCode ERR_CRYPTO_NO_SUCH_PROVIDER = NumberedException.registerCode(SYSTEM_PREFIX, 2010, "No such provider: %s", 1);
		public static final IErrorCode ERR_CRYPTO_UNKNOWN = NumberedException.registerCode(SYSTEM_PREFIX, 2999, "Unknown crypto error", 0);
		
		
	private static final Logger LOG = LoggerFactory.getLogger(CryptoUtil.class);
	private static final int AES_MAX_KEYLENGTH_BYTES;
	private static final String AES_KEY_WARNING = "Native AES with {} key length is not supported by platform. Install Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction PolicyEntity Files for better performance.";
	
	static {
		Security.addProvider(new BouncyCastleProvider());
		int aesmkl = 0;
		try {
			aesmkl = Cipher.getMaxAllowedKeyLength("AES");
			if (aesmkl < 256) 
				removeCryptographyRestrictions();
			aesmkl = Cipher.getMaxAllowedKeyLength("AES");
		} catch (NoSuchAlgorithmException e) {
		}
		AES_MAX_KEYLENGTH_BYTES = aesmkl / 8;
	}
	
	private static void removeCryptographyRestrictions() {
	    if (!isRestrictedCryptography()) {
	        LOG.info("Cryptography restrictions removal not needed");
	        return;
	    }
	    try {
	        /*
	         * Do the following, but with reflection to bypass access checks:
	         *
	         * JceSecurity.isRestricted = false;
	         * JceSecurity.defaultPolicy.perms.clear();
	         * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
	         */
	        final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
	        final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
	        final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

	        final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
	        isRestrictedField.setAccessible(true);
	        final Field modifiersField = Field.class.getDeclaredField("modifiers");
	        modifiersField.setAccessible(true);
	        modifiersField.setInt(isRestrictedField, isRestrictedField.getModifiers() & ~Modifier.FINAL);
	        isRestrictedField.set(null, false);

	        final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
	        defaultPolicyField.setAccessible(true);
	        final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField.get(null);

	        final Field perms = cryptoPermissions.getDeclaredField("perms");
	        perms.setAccessible(true);
	        ((Map<?, ?>) perms.get(defaultPolicy)).clear();

	        final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
	        instance.setAccessible(true);
	        defaultPolicy.add((Permission) instance.get(null));

	        LOG.info("Successfully removed cryptography restrictions");
	    } catch (final Exception e) {
	        LOG.warn("Failed to remove cryptography restrictions", e);
	    }
	}

	private static boolean isRestrictedCryptography() {
	    // This simply matches the Oracle JRE, but not OpenJDK.
	    return "Java(TM) SE Runtime Environment".equals(System.getProperty("java.runtime.name"));
	}
	
	////////////////////////////////////
	// SIGNATURE METHODS              //
	////////////////////////////////////
	
	public static final boolean checkAESSignature(byte[] data, byte[] signature, byte[] aesKey, byte[] asIv) throws NumberedException {
		byte[] correctSignature = getAESSignature(data, aesKey, asIv);
		if (Arrays.equals(signature, correctSignature)) 
			return true;
		return false;
	}
	
	public static final byte[] getAESSignature(byte[] data, byte[] aesKey, byte[] asIv) throws NumberedException {
		byte[] sha = getSHA1(data);
		byte[] encrypted = encryptAESCBC_PKCS7Padding(sha, aesKey, asIv);
		return encrypted;
	}
	
	public static final byte[] getSHA1(byte[] data) throws NumberedException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(data);
			byte[] digest = md.digest();
			return digest;
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		}
	}
	
	public static final byte[] getSHA256(byte[] data) throws NumberedException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(data);
			byte[] digest = md.digest();
			return digest;
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		}
	}
	
	public static byte[] generateSessionKey() throws NumberedException {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(128);
			return keygen.generateKey().getEncoded();
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		}
	}
	
	////////////////////////////////////
	// RSA METHODS                    //
	////////////////////////////////////
	
	public static final boolean checkRSASignature(byte[] data, byte[] signature, PublicKey rsaPublicKey) {
		PSSSigner pss = new PSSSigner(new RSAEngine(), new SHA1Digest(), 20);
		pss.init(true, new RSAKeyParameters(false, rsaPublicKey.getKey().getModulus(), rsaPublicKey.getKey().getPublicExponent()));
		pss.update(data,  0,  data.length);
		return pss.verifySignature(signature);
	}
	
	public static byte[] sign(byte[] data, PublicKey rsaPublicKey) {
		PSSSigner pss = new PSSSigner(new RSAEngine(), new SHA1Digest(), 20);
		pss.init(true, new ParametersWithRandom(new RSAKeyParameters(false, rsaPublicKey.getKey().getModulus(), rsaPublicKey.getKey().getPublicExponent()), new SecureRandom()));
		pss.update(data,  0,  data.length);
		try {
			return pss.generateSignature();
		} catch (DataLengthException | CryptoException e) {
			return null;
		}
	}
	
	public static byte[] encryptRSAPublicOAEP(byte[] data, PublicKey key) throws NumberedException {
		try {
			Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key.getKey());
			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM,  e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		} catch (NoSuchProviderException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PROVIDER, e, e.getMessage());
		} 
	}

	public static byte[] signRSA_SHA1(byte[] input, PrivateKey key) throws NumberedException {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			//Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key.getKey());
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(input);
			byte[] digest = md.digest();
			return cipher.doFinal(digest);			
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		}
	}

	public static byte[] encryptRSAPublic(byte[] input, PublicKey key) throws NumberedException {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			//Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key.getKey());
			return cipher.doFinal(input);
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		} 
	}

	public static byte[] decryptRSAPrivate(byte[] input, PrivateKey key) throws NumberedException {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			//Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, key.getKey());
			return cipher.doFinal(input);
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		}
	}
	
	public static byte[] decryptRSAPublic(byte[] input, PublicKey key) throws NumberedException {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			//Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, key.getKey());
			return cipher.doFinal(input);
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		}
	}
	
	
	//////////////////////////////////////////
	// AES									//
	//////////////////////////////////////////
	
	private static byte[] processAESData(BufferedBlockCipher cipher, byte[] data) throws DataLengthException, IllegalStateException, InvalidCipherTextException
	{
	    int minSize = cipher.getOutputSize(data.length);
	    byte[] outBuf = new byte[minSize];
	    int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
	    int length2 = cipher.doFinal(outBuf, length1);
	    int actualLength = length1 + length2;
	    if (outBuf.length != actualLength) {
	    	byte[] result = new byte[actualLength];
		    System.arraycopy(outBuf, 0, result, 0, result.length);
		    return result;
	    }
	    return outBuf;
	}
	
	// CBC No Padding
	
	public static byte[] encryptAESCBC_NOPAD(byte[] data, byte[] keyBytes, byte[] ivBytes) throws NumberedException
	{
		if (data == null)
			throw new NullPointerException("cipherText is null");
		if (keyBytes == null)
			throw new NullPointerException("keyBytes is null");
		if (ivBytes == null) 
			throw new NullPointerException("ivBytes is null");

		try {
			if (AES_MAX_KEYLENGTH_BYTES >= keyBytes.length) {
				SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
				IvParameterSpec iv = new IvParameterSpec(ivBytes);
				Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding"); 
				cipher.init(Cipher.ENCRYPT_MODE, key, iv);
				return cipher.doFinal(data);
			} else {
				LOG.warn(AES_KEY_WARNING, keyBytes.length * 8);
				BufferedBlockCipher aes = new BufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
				CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(keyBytes), ivBytes);
				aes.init(true, ivAndKey);
				return processAESData(aes, data);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (DataLengthException e) {
			throw new NumberedException(ERR_CRYPTO_DATA_LENGTH_ERROR, e);
		} catch (IllegalStateException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_STATE, e);
		} catch (InvalidCipherTextException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_CYPHER_TEXT, e);
		}
	}
	
	public static byte[] decryptAESCBC_NOPAD(byte[] cipherText, byte[] keyBytes, byte[] ivBytes) throws NumberedException
	{
		if (cipherText == null)
			throw new NullPointerException("cipherText is null");
		if (keyBytes == null)
			throw new NullPointerException("keyBytes is null");	
		if (ivBytes == null) 
			throw new NullPointerException("ivBytes is null");
		
		try {
			if (AES_MAX_KEYLENGTH_BYTES >= keyBytes.length) {
				SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
				IvParameterSpec iv = new IvParameterSpec(ivBytes);
				Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding"); 
				cipher.init(Cipher.DECRYPT_MODE, key, iv);
				return cipher.doFinal(cipherText);
			} else {
				LOG.warn(AES_KEY_WARNING, keyBytes.length * 8);
				
				BufferedBlockCipher aes = new BufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
				CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(keyBytes), ivBytes);
				aes.init(false, ivAndKey);
				return processAESData(aes, cipherText);
			}		    
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (DataLengthException e) {
			throw new NumberedException(ERR_CRYPTO_DATA_LENGTH_ERROR, e);
		} catch (IllegalStateException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_STATE, e);
		} catch (InvalidCipherTextException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_CYPHER_TEXT, e);
		}
	}
	
	// CBC PKCS7/PKCS5 Padding
	
	public static byte[] encryptAESCBC_PKCS7Padding(byte[] data, byte[] keyBytes, byte[] ivBytes) throws NumberedException
	{
		if (data == null)
			throw new NullPointerException("cipherText is null");
		if (keyBytes == null)
			throw new NullPointerException("keyBytes is null");
		if (ivBytes == null) 
			throw new NullPointerException("ivBytes is null");
		
		try {
			if (AES_MAX_KEYLENGTH_BYTES >= keyBytes.length) {
				SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
				IvParameterSpec iv = new IvParameterSpec(ivBytes);
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  // see http://stackoverflow.com/questions/25942165/aes-256-and-pkcs7padding-fails-in-java
				cipher.init(Cipher.ENCRYPT_MODE, key, iv);
				return cipher.doFinal(data);
			} else {
				LOG.warn(AES_KEY_WARNING, keyBytes.length * 8);
				// PKCS5/PKCS7 padding
				PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
				CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(keyBytes), ivBytes);
				aes.init(true, ivAndKey);
				return processAESData(aes, data);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (DataLengthException e) {
			throw new NumberedException(ERR_CRYPTO_DATA_LENGTH_ERROR, e);
		} catch (IllegalStateException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_STATE, e);
		} catch (InvalidCipherTextException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_CYPHER_TEXT, e);
		}
	}
	
	public static byte[] decryptAESCBC_PKCS7Padding(byte[] cipherText, byte[] keyBytes, byte[] ivBytes) throws NumberedException
	{
		if (cipherText == null)
			throw new NullPointerException("cipherText is null");
		if (keyBytes == null)
			throw new NullPointerException("keyBytes is null");	
		if (ivBytes == null) 
			throw new NullPointerException("ivBytes is null");
		
		
		try {
			if (AES_MAX_KEYLENGTH_BYTES >= keyBytes.length) {
				SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
				IvParameterSpec iv = new IvParameterSpec(ivBytes);
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
				cipher.init(Cipher.DECRYPT_MODE, key, iv);
				return cipher.doFinal(cipherText);
			} else {
				LOG.warn(AES_KEY_WARNING, keyBytes.length * 8);
				// PKCS5/PKCS7 padding
				PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
				CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(keyBytes), ivBytes);
				aes.init(false, ivAndKey);
				return processAESData(aes, cipherText);
			}		    
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (DataLengthException e) {
			throw new NumberedException(ERR_CRYPTO_DATA_LENGTH_ERROR, e);
		} catch (IllegalStateException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_STATE, e);
		} catch (InvalidCipherTextException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_CYPHER_TEXT, e);
		}
	}
	
	// ECB No Padding
	
	public static byte[] encryptAESECB_NOPAD(byte[] data, byte[] keyBytes) throws NumberedException {
		if (data == null)
			throw new NullPointerException("cipherText is null");
		if (keyBytes == null)
			throw new NullPointerException("keyBytes is null");
		
		try {
			if (AES_MAX_KEYLENGTH_BYTES >= keyBytes.length) {
				SecretKeySpec scs = new SecretKeySpec(keyBytes, "AES");
				Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
				cipher.init(Cipher.ENCRYPT_MODE, scs);
				return cipher.doFinal(data);
			} else {
				LOG.warn(AES_KEY_WARNING, keyBytes.length * 8);
				BufferedBlockCipher aes = new BufferedBlockCipher(new AESEngine());
				CipherParameters key = new KeyParameter(keyBytes);
				aes.init(true, key);
				return processAESData(aes, data);
			}
			
			
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (DataLengthException e) {
			throw new NumberedException(ERR_CRYPTO_DATA_LENGTH_ERROR, e);
		} catch (IllegalStateException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_STATE, e);
		} catch (InvalidCipherTextException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_CYPHER_TEXT, e);
		}
	}
	
	public static byte[] decryptAESECB_NOPAD(byte[] cipherText, byte[] keyBytes) throws NumberedException {
		if (cipherText == null)
			throw new NullPointerException("cipherText is null");
		if (keyBytes == null)
			throw new NullPointerException("keyBytes is null");	
		
		try {
			if (AES_MAX_KEYLENGTH_BYTES >= keyBytes.length) {
				SecretKeySpec scs = new SecretKeySpec(keyBytes, "AES");
				Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding"); 
				cipher.init(Cipher.DECRYPT_MODE, scs);
				return cipher.doFinal(cipherText);
			} else {
				LOG.warn(AES_KEY_WARNING, keyBytes.length * 8);
				BufferedBlockCipher aes = new BufferedBlockCipher(new AESEngine());
				CipherParameters key = new KeyParameter(keyBytes);
				aes.init(false, key);
				return processAESData(aes, cipherText);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (DataLengthException e) {
			throw new NumberedException(ERR_CRYPTO_DATA_LENGTH_ERROR, e);
		} catch (IllegalStateException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_STATE, e);
		} catch (InvalidCipherTextException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_CYPHER_TEXT, e);
		}
	}
	
	// ECB PKCS7/PKCS5 Padding
	
	public static  byte[] encryptAESECB_PKCS7Padding(byte[] data, byte[] keyBytes) throws NumberedException
	{
		if (data == null)
			throw new NullPointerException("cipherText is null");
		if (keyBytes == null)
			throw new NullPointerException("keyBytes is null");
		
		try {
			Cipher cipher;
			if (AES_MAX_KEYLENGTH_BYTES >= keyBytes.length) {
				SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
				cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); 
				cipher.init(Cipher.ENCRYPT_MODE, key);
			} else {
				LOG.warn(AES_KEY_WARNING, keyBytes.length * 8);
				PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new AESEngine());
				CipherParameters key = new KeyParameter(keyBytes);
				aes.init(true, key);
				return processAESData(aes, data);
			}
			
		    return cipher.doFinal(data);
		    
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (DataLengthException e) {
			throw new NumberedException(ERR_CRYPTO_DATA_LENGTH_ERROR, e);
		} catch (IllegalStateException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_STATE, e);
		} catch (InvalidCipherTextException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_CYPHER_TEXT, e);
		}
	}

	public static  byte[] decryptAESECB_PKCS7Padding(byte[] cipherText, byte[] keyBytes) throws NumberedException
	{
		if (cipherText == null)
			throw new NullPointerException("cipherText is null");
		if (keyBytes == null)
			throw new NullPointerException("keyBytes is null");	
		
		try {
			if (AES_MAX_KEYLENGTH_BYTES >= keyBytes.length) {
				SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
				Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // see http://stackoverflow.com/questions/25942165/aes-256-and-pkcs7padding-fails-in-java
				cipher.init(Cipher.DECRYPT_MODE, key);
				return cipher.doFinal(cipherText);
			} else {
				LOG.warn(AES_KEY_WARNING, keyBytes.length * 8);
				PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new AESEngine());
				CipherParameters key = new KeyParameter(keyBytes);
				aes.init(false, key);
				return processAESData(aes, cipherText);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_ALGORYTHM, e, e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_NO_SUCH_PADDING, e, e.getMessage());
		} catch (InvalidKeyException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_KEY, e);
		} catch (BadPaddingException e) {
			throw new NumberedException(ERR_CRYPTO_BAD_PADDING, e);
		} catch (IllegalBlockSizeException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_BLOCK_SIZE, e);
		} catch (DataLengthException e) {
			throw new NumberedException(ERR_CRYPTO_DATA_LENGTH_ERROR, e);
		} catch (IllegalStateException e) {
			throw new NumberedException(ERR_CRYPTO_ILLEGAL_STATE, e);
		} catch (InvalidCipherTextException e) {
			throw new NumberedException(ERR_CRYPTO_INVALID_CYPHER_TEXT, e);
		}
	   	
	}	
}
