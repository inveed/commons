package net.inveed.commons.reflection;

import java.util.Map;

public final class MapTypeDesc extends BeanTypeDesc<Map<?,?>>{

	@SuppressWarnings("unchecked")
	MapTypeDesc(Class<? extends Map<?,?>> type) {
		super((Class<Map<?, ?>>) type);
	}
}
