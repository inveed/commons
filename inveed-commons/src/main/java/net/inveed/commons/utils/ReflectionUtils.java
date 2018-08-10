package net.inveed.commons.utils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtils {
	private static final Map<Class<?>, Object> DEFAULTS;

	static {
		HashMap<Class<?>, Object> map = new HashMap<Class<?>, Object>();
		map.put(boolean.class, false);
		map.put(char.class, '\0');
		map.put(byte.class, (byte) 0);
		map.put(short.class, (short) 0);
		map.put(int.class, 0);
		map.put(long.class, 0L);
		map.put(float.class, 0f);
		map.put(double.class, 0d);
		DEFAULTS = Collections.unmodifiableMap(map);
	}
	
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A getAnnotation(Annotation[] annotations, Class<A> clazz) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(clazz)) {
                    return (A) annotation;
                }
            }
        }
        return null;
    }
	
	public static Object defaultValue(Class<?> type) {
		return DEFAULTS.get(type);
	}

}
