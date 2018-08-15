package net.inveed.commons.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.inveed.commons.reflection.ext.IBeanTypeExtension;

public final class JavaTypeRegistry {
	// STATIC
	private static final Logger LOG = LoggerFactory.getLogger(JavaTypeRegistry.class);
	private static final HashMap<Class<?>, JavaTypeDesc<?>> classMap = new HashMap<>();
	private static final ArrayList<Class<? extends IBeanTypeExtension<?>>> extensions = new ArrayList<>();
	
	@SuppressWarnings("rawtypes")
	private static final IBeanTypeExtension<?> instantiateExtension(BeanTypeDesc<?> btd, Class<? extends IBeanTypeExtension> type) { 
		try {
			Constructor<?> ctr = type.getConstructor(BeanTypeDesc.class);
			return (IBeanTypeExtension<?>) ctr.newInstance(btd);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOG.warn("Cannot instantiate extension for type " + type.getName(), e);
			return null;
		}
	}
	
	public static final List<EnumTypeDesc<?>> getEnums() {
		ArrayList<EnumTypeDesc<?>> ret = new ArrayList<>();
		for (JavaTypeDesc<?> td : classMap.values()) {
			if (td instanceof EnumTypeDesc<?>) {
				ret.add((EnumTypeDesc<?>) td);
			}
		}
		return ret;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final void registerExtension(Class<? extends IBeanTypeExtension> type) {
		for (JavaTypeDesc<?> t : classMap.values()) {
			if (t instanceof BeanTypeDesc<?>) {
				BeanTypeDesc btd = (BeanTypeDesc) t;
				IBeanTypeExtension bte = instantiateExtension(btd, type);
				if (bte == null) {
					continue;
				}
				if (!bte.isValid()) {
					continue;
				}
				btd.registerExtension(bte);
			}
		}
		extensions.add((Class<? extends IBeanTypeExtension<?>>) type);
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> JavaTypeDesc<T> getType(Class<T> type) {
		if (classMap.containsKey(type)) {
			return (JavaTypeDesc<T>) classMap.get(type);
		}
		synchronized (classMap) {
			if (classMap.containsKey(type)) {
				return (JavaTypeDesc<T>) classMap.get(type);
			}

			JavaTypeDesc<?> t = null;
			if (type instanceof Class) {
				t = createFor((Class<?>) type);
			}
			classMap.put(type, t);
			
			return (JavaTypeDesc<T>) t;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static final <T> JavaTypeDesc<T> createFor(Class<T> type) {
		if (type.isPrimitive() || type == Class.class || type == Byte.class || type == Short.class
				|| type == Integer.class || type == Long.class || type == Float.class || type == Double.class
				|| type == Boolean.class || type == Character.class || type == String.class || type == UUID.class || type==java.util.Date.class) {
			return new NativeTypeDesc<T>(type);
		}

		if (type.isArray()) {
			JavaTypeDesc<?> elementType = JavaTypeRegistry.getType(type.getComponentType());
			return new ArrayTypeDesc<T, Object>(type, (JavaTypeDesc<Object>) elementType);
		} else if (type.isEnum()) { 
			return (JavaTypeDesc<T>) new EnumTypeDesc<>((Class<Enum>) type);
		} else if (Collection.class.isAssignableFrom(type)) {
			return (JavaTypeDesc<T>) new ListTypeDesc((Class<? extends Collection<?>>) type);
		} else if (Map.class.isAssignableFrom(type)) {
			return (JavaTypeDesc<T>) new MapTypeDesc((Class<? extends Map<?, ?>>) type);
		} else {
			BeanTypeDesc<T> ret = new BeanTypeDesc<T>(type);
			for (Class<? extends IBeanTypeExtension<?>> etype : extensions) {
				IBeanTypeExtension<?> e = instantiateExtension(ret, etype);
				if (e.isValid())
					ret.registerExtension((IBeanTypeExtension<T>) e);
			}
			return ret;
		}
	}
	
	private JavaTypeRegistry() {}
}
