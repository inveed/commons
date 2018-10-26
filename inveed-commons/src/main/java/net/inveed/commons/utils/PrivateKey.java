package net.inveed.commons.utils;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class PrivateKey {
	
	private final byte[] keyBytes;
	private final java.security.PrivateKey privateKey;
	private String pem;
	
	public PrivateKey(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
		this.keyBytes = keyBytes;
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		this.privateKey =  (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
	}
	
	public PrivateKey(String pem) throws IOException {
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		PEMParser parser = new PEMParser(new StringReader(pem));
		try {
			PEMKeyPair keyPair = (PEMKeyPair) parser.readObject();
			PrivateKeyInfo pkInfo = keyPair.getPrivateKeyInfo();
			this.privateKey = converter.getPrivateKey(pkInfo);
			this.keyBytes = this.privateKey.getEncoded();
			this.pem = pem;
		} finally {
			parser.close();
		}
	}
	
	public java.security.PrivateKey getKey() {
		return this.privateKey;
	}
	
	public byte[] getKeyBytes() {
		return this.keyBytes;
	}
	
	public String getPEM() {
		return this.pem;
	}
}
