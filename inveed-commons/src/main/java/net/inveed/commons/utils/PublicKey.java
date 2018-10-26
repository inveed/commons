package net.inveed.commons.utils;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PublicKey implements Serializable {
	
	private static final long serialVersionUID = -1495324611486413612L;

	private final byte[] keyBytes;
	private final RSAPublicKey key;
	
	public PublicKey(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
		this.keyBytes = keyBytes;
		this.key = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
	}
	
	public RSAPublicKey getKey() {
		return this.key;
	}
	public byte[] getKeyBytes() {
		return this.keyBytes;
	}
}
