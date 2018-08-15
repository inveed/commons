package net.inveed.commons.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.inveed.commons.reflection.annotation.PropertyGetter;

public class PropertyUtils {
	
	private static final boolean isCasableChar(char c) {
		return Character.isUpperCase(c) || Character.isLowerCase(c);
	}
	public static final String normalizePropertyName(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() >= 2
        		&& Character.isUpperCase(name.charAt(0))
        		&& !isCasableChar(name.charAt(1))){
        	return name;
        }
        if (name.length() > 2 
        		&& Character.isUpperCase(name.charAt(0))
        		&& Character.isUpperCase(name.charAt(1)) 
                && (Character.isUpperCase(name.charAt(2)) || !isCasableChar(name.charAt(2)))){
        	return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
	
	public static final AccessLevel getAccessLevel(Method m) {
		if (m == null) {
			return AccessLevel.ABSENT;
		}
		int mod = m.getModifiers();
		if (Modifier.isPublic(mod)) {
			return AccessLevel.PUBLIC;
		} else if (Modifier.isProtected(mod)) {
			return AccessLevel.PROTECTED;
		} else if (Modifier.isPrivate(mod)) {
			return AccessLevel.PRIVATE;
		} else {
			return AccessLevel.DEFAULT;
		}
	}
	
	public static final AccessLevel getAccessLevel(Field f) {
		if (f == null) {
			return AccessLevel.ABSENT;
		}
		int mod = f.getModifiers();
		if (Modifier.isPublic(mod)) {
			return AccessLevel.PUBLIC;
		} else if (Modifier.isProtected(mod)) {
			return AccessLevel.PROTECTED;
		} else if (Modifier.isPrivate(mod)) {
			return AccessLevel.PRIVATE;
		} else {
			return AccessLevel.DEFAULT;
		}
	}
	
	public static final AccessibleObject select(Field field, Method method, int requiredFieldLevel, int requiredMethodLevel) {
		if (field == null && method == null) {
			return null;
		}
		
		int fieldLvl = getAccessLevel(field).getLevel();
		int methodLvl = getAccessLevel(method).getLevel();
		
		boolean canUseField = field != null ? fieldLvl >= requiredFieldLevel : false;
		boolean canUseMethod = method != null ? methodLvl >= requiredMethodLevel : false;
		
		boolean hasFieldAnnotation = field != null ? field.getAnnotation(PropertyGetter.class) != null : false;
		boolean hasMethodAnnotation = method != null ? method.getAnnotation(PropertyGetter.class) != null : false;
		
		if (!(canUseField || hasFieldAnnotation) && !(canUseMethod || hasMethodAnnotation)) {
			return null;
		}
		
		if (!(canUseField || hasFieldAnnotation) && (canUseMethod || hasMethodAnnotation)) {
			return method;
		}
		
		if ((canUseField || hasFieldAnnotation) && !(canUseMethod || hasMethodAnnotation)) {
			return field;
		}
		
		
		if (hasFieldAnnotation && !hasMethodAnnotation) {
			return field;
		} else if (hasMethodAnnotation && !hasFieldAnnotation) {
			return method;
		}
			
		if (canUseField && !canUseMethod) {
			return field;
		} else if (canUseMethod && !canUseField) {
			return method;
		} else {
			if (fieldLvl > methodLvl) {
				return field;
			} else if (methodLvl > fieldLvl) {
				return method;
			} else {
				return method;
			}
		}
	}
}
