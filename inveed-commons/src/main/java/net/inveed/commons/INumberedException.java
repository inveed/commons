package net.inveed.commons;

public interface INumberedException {
	public interface IErrorCode {
		String getCode();
		String getPrefix();
		int getNumber();
		String getText();
		int getArgumentsNumber();
		long getLongValue();
	}
	String getMessage();
	IErrorCode getCode();
	Object[] getArgs();
}
