package net.inveed.commons.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import net.inveed.commons.reflection.annotation.EnumDisplayValue;

public final class EnumTypeDesc<T extends Enum<T>> extends JavaTypeDesc<T> {
	private static final HashMap<String, EnumTypeDesc<?>> registeredEnums = new HashMap<>();

	public static final EnumTypeDesc<?> getEnum(String name) {
		return registeredEnums.get(name);
	}
	
	private Class<T> type;
	
	private AccessibleObject displayValueGetter;
	
	EnumTypeDesc(Class<T> type) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}
		this.type = type;
		this.parse();
		registeredEnums.put(this.getName(), this);
	}
	
	private void parse() {
		for (Method m : this.type.getMethods()) {
			if (Modifier.isStatic(m.getModifiers())) {
				continue;
			}
			if (!Modifier.isPublic(m.getModifiers())) {
				continue;
			}
			if (m.getReturnType() != String.class || m.getParameterCount() > 0) {
				continue;
			}
			if (m.getAnnotation(EnumDisplayValue.class) != null) {
				displayValueGetter = m;
				break;
			}
		}
		if (displayValueGetter == null) {
			for (Field f : this.type.getFields()) {
				if (Modifier.isAbstract(f.getModifiers())) {
					continue;
				}
				if (f.getType() != String.class) {
					continue;
				}
				if (f.getAnnotation(EnumDisplayValue.class) != null) {
					displayValueGetter = f;
					break;
				}
			}
 		}
	}
	@Override
	public Class<T> getType() {
		return type;
	}
	public String getName() {
		return this.type.getSimpleName();
	}
	
	public T getEnumValue(String val) {
		T ret = Enum.valueOf(this.getType(), val);
		if (ret == null) {
			System.err.println("Cannot find enum value " + val + " for type " + this.getName());
		}
		return ret;
	}
	
	public String getDisplayValue(T value) {
		if (value == null) {
			return null;
		}
		if (this.displayValueGetter == null) {
			return value.toString();
		} else {
			if (this.displayValueGetter instanceof Method) {
				Method m = (Method) this.displayValueGetter;
				try {
					return (String) m.invoke(value);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					//TODO: LOG!
					return value.toString();
				}
			} else {
				Field f = (Field) this.displayValueGetter;
				try {
					return (String) f.get(value);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					//TODO: LOG!
					return value.toString();
				}
			}
		}
	}
	
	public T[] getDeclaredValues() {
		return this.getType().getEnumConstants();
	}

	@SuppressWarnings("unchecked")
	public String getDisplayValue(Object e) {
		if (e == null) {
			return null;
		}
		if (!this.getType().isAssignableFrom(e.getClass())) {
			return null;
		} else {
			return this.getDisplayValue((T) e);
		}
	}
}
