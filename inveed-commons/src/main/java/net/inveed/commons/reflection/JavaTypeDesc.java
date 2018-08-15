package net.inveed.commons.reflection;

import java.util.HashMap;

import net.inveed.commons.reflection.ext.IBeanTypeExtension;

public abstract class JavaTypeDesc<T>{

	private boolean _initialized = false;
	private final HashMap<Class<IBeanTypeExtension<T>>, IBeanTypeExtension<T>> extensions = new HashMap<>();
	
	public boolean isAssignableFrom(JavaTypeDesc<?> type) {
		return this.getType().isAssignableFrom(type.getType());
	}
	
	protected void initialize() {
		for (IBeanTypeExtension<T> e : this.extensions.values()) {
			e.initialize();
		}
		this._initialized = true;
	}
	public abstract Class<T> getType();
	
	public boolean isEqualToClass(Class<?> klass) {
		return this.getType() == klass;
	}
	
	protected boolean isInitialized() {
		return this._initialized;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends IBeanTypeExtension<T>> E getExtension(Class<E> type) {
		if (!this._initialized) {
			this.initialize();
		}
		return (E) this.extensions.get(type);
	}

	@SuppressWarnings("unchecked")
	public void registerExtension(IBeanTypeExtension<T> bte) {
		this.extensions.put((Class<IBeanTypeExtension<T>>) bte.getClass(), bte);
		
	}
}
