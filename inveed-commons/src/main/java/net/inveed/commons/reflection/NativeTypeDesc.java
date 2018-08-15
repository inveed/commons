package net.inveed.commons.reflection;

import java.util.Date;
import java.util.UUID;

public final class NativeTypeDesc<T> extends JavaTypeDesc<T> {
	
	private Class<T> type;
	NativeTypeDesc(Class<T> type) {
		this.type = type;
	}
	@Override
	public Class<T> getType() {
		return this.type;
	}
	
	public boolean isByte () {
		return type == byte.class || type == Byte.class;
	}
	public boolean isShort() {
		return (type == short.class || type == Short.class);
	}
	
	public boolean isInt() {
		return type == Integer.class || type == int.class;
	}
		
	public boolean isLong() {
		return type == long.class || type == Long.class;
	}

	public boolean isFloat() {
		return type == float.class || type == Float.class;
	}
	public boolean isDouble() {
		return type == double.class || type == Double.class;
	}
	
	public boolean isBoolean() {
		return type == boolean.class || type == Boolean.class;
	}
	
	public boolean isChar() {
		return type == char.class || type == Character.class;
	}
	
	public boolean isString() {
		return type == String.class;
	}
	
	public boolean isDate() {
		return type == Date.class;
	}
	
	public boolean isUUID() {
		return type == UUID.class;
	}

	@Override
	public boolean isAssignableFrom(JavaTypeDesc<?> type) {
		if (this.getType() == Long.class && type.getType() == long.class) {
			return true;
		}
		if (this.getType() == Integer.class && type.getType() == int.class) {
			return true;
		}
		if (this.getType() == Short.class && type.getType() == short.class) {
			return true;
		}
		if (this.getType() == Byte.class && type.getType() == byte.class) {
			return true;
		}
		if (this.getType() == Double.class && type.getType() == double.class) {
			return true;
		}
		if (this.getType() == Float.class && type.getType() == float.class) {
			return true;
		}
		if (this.getType() == Character.class && type.getType() == char.class) {
			return true;
		}
		return super.isAssignableFrom(type);
	}
}
