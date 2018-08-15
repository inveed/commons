package net.inveed.commons.reflection.ext;

import net.inveed.commons.reflection.BeanTypeDesc;

public interface IBeanTypeExtension<T> {
	boolean isValid();
	BeanTypeDesc<T> getBeanType();
	void initialize();
}
