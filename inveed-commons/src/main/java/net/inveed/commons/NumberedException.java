package net.inveed.commons;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.inveed.commons.INumberedException.IErrorCode;

public class NumberedException extends Exception {
	public static class ErrorCode implements IErrorCode {
		private final String code;
		private final String prefix;
		private final int number;
		private final String text;
		private final int argumentsNumber;
		private final long longValue;
		
		private ErrorCode(String prefix, int number, String text, int argNumber) {
			
			this.code = prefix + "-" + CODE_FORMATTER.format(number);
			this.prefix = prefix;
			this.number = (short) number;
			this.text = text;
			this.argumentsNumber = argNumber;
			
			long lv = 0L;
			char[] ca = new char[4];
			char[] pca = prefix.toCharArray();
			if (pca.length > ca.length) {
				throw new IllegalArgumentException("prefix should be max 4 charachters");
			}
			System.arraycopy(pca, 0, ca, ca.length - pca.length, pca.length);
			for (int i = 0; i < ca.length; i++) {
				lv = lv + 0L | ca[i];
				lv = lv << 8;
			}
			
			lv = lv << 8;
			lv = lv | (number & 0xFFFF);
			longValue = lv;
		}

		@Override
		public String getCode() {
			return code;
		}

		@Override
		public String getPrefix() {
			return prefix;
		}

		@Override
		public int getNumber() {
			return number;
		}

		@Override
		public String getText() {
			return text;
		}

		@Override
		public int getArgumentsNumber() {
			return argumentsNumber;
		}

		@Override
		public long getLongValue() {
			return longValue;
		}
	}
	
	private static final HashMap<String, ErrorCode> registeredCodes = new HashMap<>();
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static final DecimalFormat CODE_FORMATTER = new DecimalFormat("#####");
	
	private static final IErrorCode ERROR_REGISTERED = new ErrorCode(SystemErrorCodes.SYSTEM_PREFIX, 0, "ErrorCode already registered: %s", 1);
	
	public static final IErrorCode registerCode(String prefix, int number, String text, int argsCount) {
		lock.writeLock().lock();
		try {
			if (prefix == null)
				throw new NullPointerException("prefix");
			if (number <= 0) {
				throw new IllegalArgumentException("number is less then or equal zero");
			}
			if (text == null) {
				throw new NullPointerException("text");
			}
			prefix = prefix.trim().toUpperCase();
			
			text = text.trim();
			
			if (prefix.length() == 0) {
				throw new IllegalArgumentException("prefix is empty");
			}
			if (prefix.length() > 4) {
				throw new IllegalArgumentException("prefix should be max 4 charachters");
			}
			if (!prefix.matches("[A-Z0-9]+")) {
				throw new IllegalArgumentException("prefix should contain only letters and numbers");
			}
			if (text.length() == 0) {
				throw new IllegalArgumentException("text is empty");
			}
			if (number < 0 || number > 0xFFFF) {
				throw new IllegalArgumentException("number should be between 0 and 65535");
			}
			ErrorCode ret = new ErrorCode(prefix, number, text, argsCount);
			if (registeredCodes.containsKey(ret.getCode())) {
				throw new IllegalArgumentException(String.format(ERROR_REGISTERED.getText(), ret.getCode()));
			}
			registeredCodes.put(ret.getCode(), ret);
			return ret;
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public static final IErrorCode getCode(String prefix, int number) {
		lock.readLock().lock();
		try {
			if (prefix == null)
				throw new NullPointerException("prefix");
			if (number <= 0) {
				throw new IllegalArgumentException("number is less then or equal zero");
			}
			prefix = prefix.trim().toUpperCase();
			
			if (prefix.length() == 0) {
				throw new IllegalArgumentException("prefix is empty");
			}
			if (!prefix.matches("[A-Z0-9]+")) {
				throw new IllegalArgumentException("prefix should contain only letters and numbers");
			}
			String code = prefix + "-" + CODE_FORMATTER.format(number);
			return registeredCodes.get(code);
			
		} finally {
			lock.readLock().unlock();
		}
	}
	
	private static String getMessage(IErrorCode code, Object[] args) {
		if (code == null) 
			throw new NullPointerException("code");
		if (args == null)
			return code.getText();
		try {
			return String.format(code.getText(), args);
		} catch (Throwable e) {
			StringBuffer sb = new StringBuffer();
			sb.append(code.getText());
			sb.append(" WITH_ARGS: ");
			for (Object o: args) {
				sb.append("'");
				sb.append(o);
				sb.append("',");
			}
			return sb.toString();
		}
	}
	
	private static final long serialVersionUID = 7603960078552903686L;

	private final IErrorCode code;
	private final Object[] args;
	
	public NumberedException(IErrorCode code) {
		super(getMessage(code, null));
		this.code = code;
		this.args = null;
	}
	
	public NumberedException(IErrorCode code, Object ... args) {
		super(getMessage(code, args));
		this.code = code;
		this.args = args;
	}
	
	public NumberedException(IErrorCode code, Throwable cause ) {
		super(getMessage(code, null), cause);
		this.code = code;
		this.args = null;
		
	}
	
	public NumberedException(IErrorCode code, Throwable cause, Object ... args) {
		super(getMessage(code, args), cause);
		this.code = code;
		this.args = args;
		
	}
	
	public IErrorCode getCode() {
		return code;
	}
	
	public Object[] getArgs() {
		return this.args;
	}
}
