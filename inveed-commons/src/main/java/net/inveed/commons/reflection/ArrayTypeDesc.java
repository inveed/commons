package net.inveed.commons.reflection;

/**
 * Descriptor of array types
 *
 */
public final class ArrayTypeDesc<T, E> extends JavaTypeDesc<T> {
	private final JavaTypeDesc<E> elementType;
	private final Class<T> arrayType;
	
	ArrayTypeDesc(Class<T> arrayType, JavaTypeDesc<E> elementType) {
		this.elementType = elementType;
		this.arrayType = arrayType;
	}
	
	public JavaTypeDesc<E> getElementType() {
		return this.elementType;
	}

	@Override
	public Class<T> getType() {
		return this.arrayType;
	}
}
