package net.inveed.commons.reflection;

import java.util.Collection;

public final class ListTypeDesc extends BeanTypeDesc<Collection<?>>{
	
	@SuppressWarnings("unchecked")
	ListTypeDesc(Class<? extends Collection<?>> type) {
		super((Class<Collection<?>>) type);
	}
}
