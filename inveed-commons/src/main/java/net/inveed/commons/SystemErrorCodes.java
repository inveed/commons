package net.inveed.commons;

import net.inveed.commons.INumberedException.IErrorCode;

public class SystemErrorCodes {

	static final String SYSTEM_PREFIX = "SYS";
	
	//2000-2999 - CryptoErrors
	public static IErrorCode ERR_CRYPTO_NO_SUCH_ALGORYTHM = NumberedException.registerCode(SYSTEM_PREFIX, 2001, "No such algorythm: %s", 1);
	public static IErrorCode ERR_CRYPTO_NO_SUCH_PADDING = NumberedException.registerCode(SYSTEM_PREFIX, 2002, "No such padding: %s", 1);
	public static IErrorCode ERR_CRYPTO_BAD_PADDING = NumberedException.registerCode(SYSTEM_PREFIX, 2003, "Bad padding", 0);
	public static IErrorCode ERR_CRYPTO_INVALID_KEY = NumberedException.registerCode(SYSTEM_PREFIX, 2004, "Invalid key", 0);
	public static IErrorCode ERR_CRYPTO_ILLEGAL_BLOCK_SIZE = NumberedException.registerCode(SYSTEM_PREFIX, 2005, "Illegal block size", 0);
	public static IErrorCode ERR_CRYPTO_INVALID_KEY_SPEC = NumberedException.registerCode(SYSTEM_PREFIX, 2006, "Invalid key spec", 0);
	public static IErrorCode ERR_CRYPTO_DATA_LENGTH_ERROR = NumberedException.registerCode(SYSTEM_PREFIX, 2007, "Invalid data length", 0);
	public static IErrorCode ERR_CRYPTO_ILLEGAL_STATE = NumberedException.registerCode(SYSTEM_PREFIX, 2008, "Illegal state", 0);
	public static IErrorCode ERR_CRYPTO_INVALID_CYPHER_TEXT = NumberedException.registerCode(SYSTEM_PREFIX, 2009, "Invalid cypher text", 0);
	public static IErrorCode ERR_CRYPTO_NO_SUCH_PROVIDER = NumberedException.registerCode(SYSTEM_PREFIX, 2010, "No such provider: %s", 1);
	public static IErrorCode ERR_CRYPTO_UNKNOWN = NumberedException.registerCode(SYSTEM_PREFIX, 2999, "Unknown crypto error", 0);
	
	private SystemErrorCodes() {
	}
}
