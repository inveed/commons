package net.inveed.commons.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.inveed.commons.reflection.annotation.ParameterName;

public class TypeUtils {
	public static final class ArgumentMatch {

		private final int score;
		private final Object[] sorted;

		public ArgumentMatch(int score, Integer[] hardParamMap, Object[] values, Parameter[] params) {
			this.score = score;

			Object[] sorted = new Object[values.length];

			for (int i = 0; i < params.length; i++) {
				sorted[i] = values[hardParamMap[i]];
			}
			this.sorted = sorted;
		}

		public int getScore() {
			return this.score;
		}

		public Object[] getSorted() {
			return this.sorted;
		}

	}
	
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
	
	public static Object toObject(JavaTypeDesc<?> type, Object id) {
		if (id == null) {
			return null;
		}
		return toObject(type, id.toString());
	}

	public static Object toObject(JavaTypeDesc<?> t, String id) {

		if (t instanceof NativeTypeDesc) {
			NativeTypeDesc<?> type = (NativeTypeDesc<?>) t;

			if (type.isString())
				return id;
			else if (type.isBoolean())
				return Boolean.parseBoolean(id);
			else if (type.isByte())
				return Byte.parseByte(id);
			else if (type.isShort())
				return Short.parseShort(id);
			else if (type.isInt())
				return Integer.parseInt(id);
			else if (type.isLong())
				return Long.parseLong(id);
			else if (type.isFloat())
				return Float.parseFloat(id);
			else if (type.isDouble())
				return Double.parseDouble(id);
			else if (type.isUUID())
				return UUID.fromString(id);

		}
		return null;
	}

	public static ArgumentMatch getArgumentMatches(Object[] values, Parameter[] params, String[] nameHints) {

		if (values == null) {
			return null;
		}

		if (params == null) {
			return null;
		}
		if (params.length != values.length) {
			return null;
		}
		if (nameHints == null) {
			nameHints = new String[values.length];
		}

		Integer[] hardParamMap = new Integer[params.length];
		Integer[] hardValueMap = new Integer[values.length];
		boolean[] needConvert = new boolean[values.length];

		for (int ni = 0; ni < nameHints.length; ni++) {
			String name = nameHints[ni];
			if (name == null) {
				continue;
			}
			Class<?> vtype = null;
			if (values[ni] != null) {
				vtype = values[ni].getClass();
			}
			for (int pi = 0; pi < params.length; pi++) {
				Parameter p = params[pi];
				ParameterName annArgName = p.getAnnotation(ParameterName.class);
				if (annArgName == null) {
					continue;
				}
				if (annArgName.value().equals(name)) {
					// Параметр подходит по имени. Надо проверить по типу.

					if (vtype != null) {
						if (p.getType().isAssignableFrom(vtype)) {
							hardParamMap[pi] = ni;
							hardValueMap[ni] = pi;
							break;
						}
						Object convValue = TypeUtils.toObject(JavaTypeRegistry.getType(p.getType()), values[ni]);
						if (convValue != null) {
							if (p.getType().isAssignableFrom(convValue.getClass())) {
								hardParamMap[pi] = ni;
								hardValueMap[ni] = pi;
								needConvert[ni] = true;
								break;
							} else {
								// TODO: LOG!
								return null;// Имя задано, но присвоить значение
											// мы не сможем.
							}
						} else {
							if (p.getType().isPrimitive()) {
								// TODO: LOG!
								return null; // Имя задано, но присвоить
												// значение мы не сможем.
							} else {
								break;
							}
						}
					}
				}
			}
		}

		boolean cont = true;
		int globalScore = 0;
		while (cont) {
			cont = false;
			while (true) {
				int score = tryAddNotNullToMap(values, params, hardParamMap, hardValueMap);
				if (score > 0) {
					globalScore += (score * 5);
					cont = true;
				} else {
					break;
				}
			}
			int score = tryAddNotNullConvertedToMap(values, params, hardParamMap, hardValueMap, needConvert);
			if (score > 0) {
				globalScore += (score * 2);
				cont = true;
			}

			score = tryAddNullToMap(values, params, hardParamMap, hardValueMap);
			if (score > 0) {
				globalScore += score;
				cont = true;
			}
		}

		boolean ok = true;
		for (Integer i : hardValueMap) {
			if (i == null) {
				ok = false;
			}
		}

		if (!ok) {
			return null;
		}

		return new ArgumentMatch(globalScore, hardParamMap, values, params);
	}

	private static int tryAddNullToMap(Object[] values, Parameter[] params, Integer[] hardParamMap,
			Integer[] hardValueMap) {
		int ret = 0;
		for (int pi = 0; pi < hardParamMap.length; pi++) {
			if (hardParamMap[pi] != null) {
				continue;
			}

			Parameter p = params[pi];
			Integer supportedValueIndex = null;
			boolean ok = true;
			for (int vi = 0; vi < hardValueMap.length; vi++) {
				if (hardValueMap[vi] != null) {
					continue;
				}
				Object val = values[vi];
				if (val == null) {
					if (!p.getType().isPrimitive()) {
						if (supportedValueIndex != null) {
							ok = false;
							break;
						} else {
							supportedValueIndex = vi;
						}
					}
				}
			}
			if (ok && supportedValueIndex != null) {
				hardValueMap[supportedValueIndex] = pi;
				hardParamMap[pi] = supportedValueIndex;
				ret++;
			}
		}
		return ret;
	}

	private static int tryAddNotNullConvertedToMap(Object[] values, Parameter[] params, Integer[] hardParamMap,
			Integer[] hardValueMap, boolean[] needConvert) {
		int ret = 0;
		for (int pi = 0; pi < hardParamMap.length; pi++) {
			if (hardParamMap[pi] != null) {
				continue;
			}

			Parameter p = params[pi];
			Integer supportedValueIndex = null;
			boolean ok = true;
			for (int vi = 0; vi < hardValueMap.length; vi++) {
				if (hardValueMap[vi] != null) {
					continue;
				}
				Object val = values[vi];
				if (val == null) {
					continue;
				}
				Object convVal = TypeUtils.toObject(JavaTypeRegistry.getType(p.getType()), val);
				if (convVal == null) {
					continue;
				}
				if (p.getType().isAssignableFrom(convVal.getClass())) {
					if (supportedValueIndex != null) {
						ok = false;
						break;
					} else {
						supportedValueIndex = vi;
					}
				}

			}
			if (ok && supportedValueIndex != null) {
				hardValueMap[supportedValueIndex] = pi;
				hardParamMap[pi] = supportedValueIndex;
				needConvert[supportedValueIndex] = true;
				ret++;
			}
		}
		return ret;
	}

	private static int tryAddNotNullToMap(Object[] values, Parameter[] params, Integer[] hardParamMap,
			Integer[] hardValueMap) {
		int ret = 0;
		for (int pi = 0; pi < hardParamMap.length; pi++) {
			if (hardParamMap[pi] != null) {
				continue;
			}

			Parameter p = params[pi];
			Integer supportedValueIndex = null;
			boolean ok = true;
			for (int vi = 0; vi < hardValueMap.length; vi++) {
				if (hardValueMap[vi] != null) {
					continue;
				}
				Object val = values[vi];
				if (val == null) {
					continue;
				}
				if (p.getType().isAssignableFrom(val.getClass())) {
					if (supportedValueIndex != null) {
						ok = false;
						break;
					} else {
						supportedValueIndex = vi;
					}
				}
			}
			if (ok && supportedValueIndex != null) {
				hardValueMap[supportedValueIndex] = pi;
				hardParamMap[pi] = supportedValueIndex;
				ret++;
			}
		}
		return ret;
	}

	
}
