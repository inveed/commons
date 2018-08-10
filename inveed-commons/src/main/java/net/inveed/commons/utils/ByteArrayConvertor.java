package net.inveed.commons.utils;

import java.nio.charset.Charset;
import java.util.UUID;

public class ByteArrayConvertor {

	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	public static final byte[] intToByteArray(int value) {
	    return new byte[] {
	            (byte)((value >>> 24) & 0xFF),
	            (byte)((value >>> 16) & 0xFF),
	            (byte)((value >>> 8) & 0xFF),
	            (byte)(value & 0xFF)};
	}
	
	public static final int byteArrayToInt(byte[] bytes) {
		return ((int) (bytes[0] & 0xFF)) << 24 | ((int)(bytes[1] & 0xFF)) << 16 | ((int)(bytes[2] & 0xFF)) << 8 | ((int)(bytes[3] & 0xFF));
	}
	
	public static final byte[] intTo2Octet(int value) {
	    return new byte[] {
	    		(byte)((value >>> 8) & 0xFF),
	    		(byte)(value & 0xFF)};
	}
	
	public static int intFrom2Octets(byte[] bytes) {
	     return (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
	}
	
	public static final byte[] longTo4Octets(long value) {
	    return new byte[] {
	            (byte)((value >>> 24) & 0xFFL),
	            (byte)((value >>> 16) & 0xFFL),
	            (byte)((value >>> 8) & 0xFFL),
	            (byte)(value & 0xFFL)};
	}
	
	public static final byte[] longToByteArray(long value) {
	    return new byte[] {
	    		(byte)((value >>> 56) & 0xFFL),
	    		(byte)((value >>> 48) & 0xFFL),
	    		(byte)((value >>> 40) & 0xFFL),
	    		(byte)((value >>> 32) & 0xFFL),
	            (byte)((value >>> 24) & 0xFFL),
	            (byte)((value >>> 16) & 0xFFL),
	            (byte)((value >>> 8) & 0xFFL),
	            (byte)(value & 0xFFL)};
	}
	
	public static final long byteArrayToLong(byte[] bytes) {
		return ((long) (bytes[0] & 0xFF)) << 56 
				| ((long) (bytes[1] & 0xFF)) << 48
				| ((long) (bytes[2] & 0xFF)) << 40
				| ((long) (bytes[3] & 0xFF)) << 32 
				| ((long) (bytes[4] & 0xFF)) << 24 
				| ((long)(bytes[5] & 0xFF)) << 16 
				| ((long)(bytes[6] & 0xFF)) << 8 
				| ((long)(bytes[7] & 0xFF));
	}

	public static long longFrom4Octets(byte[] bytes) {
		return ((long) (bytes[0] & 0xFF)) << 24 | ((long)(bytes[1] & 0xFF)) << 16 | ((long)(bytes[2] & 0xFF)) << 8 | ((long)(bytes[3] & 0xFF));
	}
	
	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for(byte b: a)
				sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}

	public static byte[] uuidToByteArray(UUID serviceId) {
		long m = serviceId.getMostSignificantBits();
		long l = serviceId.getLeastSignificantBits();
		
		byte[] ret = new byte[16];
		byte[] mb = longToByteArray(m);
		byte[] ml = longToByteArray(l);
		System.arraycopy(mb, 0, ret, 0, 8);
		System.arraycopy(ml, 0, ret, 8, 8);
		return ret;
	}

	public static UUID uuidFromByteArray(byte[] b) {
		if (b == null)
			return null;
		if (b.length < 16)
			return null;
		byte[] mb = new byte[8];
		byte[] ml = new byte[8];
		System.arraycopy(b, 0, mb, 0, 8);
		System.arraycopy(b, 8, ml, 0, 8);
		long m = byteArrayToLong(mb);
		long l = byteArrayToLong(ml);
		
		return new UUID(m, l);
	}
	
	public static String bytesToHex(byte[] bytes, String separator, boolean skipZero) {
		StringBuffer sb = new StringBuffer();
	    boolean wasPrinted = false;
	    for ( int j = 0; j < bytes.length; j++ ) {
	    	int v = bytes[j] & 0xFF;
	    	if (skipZero && !wasPrinted && v == 0)
	    		continue;
	    	wasPrinted = true;
	        
	        sb.append(hexArray[v >>> 4]);
	        sb.append(hexArray[v & 0x0F]);
	        if (j < bytes.length - 1)
	        	sb.append(separator);
	    }
	    return sb.toString();
	}
	
	public static String bytesToHexMultiline(byte[] bytes, String prefix) {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
	    int cnt1 = 0;
	    for ( int j = 0; j < bytes.length; j++ ) {
	    	int v = bytes[j] & 0xFF;
	    	cnt1++;
	        sb.append(hexArray[v >>> 4]);
	        sb.append(" ");
	        sb.append(hexArray[v & 0x0F]);
	        sb.append(" ");
	        
	        if (cnt1 % 16 == 0) {
	        	sb.append("\n");
	        	sb.append(prefix);
	        } else if (cnt1 % 4 == 0) {
	        	sb.append(" ");
	        }
	    }
	    return sb.toString();
	}
	
	public static byte[] hexStringToBytes(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	    	char c1 = s.charAt(i);
	    	char c2 = s.charAt(i+1);
	    	int d1 = Character.digit(c1, 16);
	    	int d2 = Character.digit(c2, 16);
	    	if (d1 < 0 || d2 < 0) {
	    		throw new NumberFormatException(s + " is not hex value");
	    	}
	        data[i / 2] = (byte) ((d1 << 4) + d2);
	    }
	    return data;
	}
}
