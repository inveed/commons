package net.inveed.commons.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.inveed.commons.reflection.ext.IBeanPropertyExtension;

/**
 * Descriptor of bean property
 *
 */
public final class BeanPropertyDesc {
	private static final Logger LOG = LoggerFactory.getLogger(BeanPropertyDesc.class);
	private final Object _lock = new Object();
	
	private final HashMap<Class<? extends IBeanPropertyExtension>, IBeanPropertyExtension> extensions = new HashMap<>();
	
	// Container type
	private final BeanTypeDesc<?> beanType;
	
	// Property name
	private final String 		name;
	
	// Property order
	private int 				order;

	// Property type
	private JavaTypeDesc<?>		propertyType;
	
	// Superclass property
	private BeanPropertyDesc 	overridedProperty 		= null;
	private boolean 			overridedPropertySet	= false;
	
	// Property "parts" - related field, getter and setter.
	private Field	field;
	private Method	getter;
	private Method 	setter;
	
	private AccessibleObject actualGetter;
	private AccessibleObject actualSetter;
	
	/// Constructor and initialization
	BeanPropertyDesc(String name, BeanTypeDesc<?> container) {
		this.name = PropertyUtils.normalizePropertyName(name);
		this.beanType = container;
	}
	
	// Validates possibility to use some getter/setter/field as part of this property.
	protected boolean validateType(Class<?> t) {
		if (this.getType() == null) {
			// Current type cannot be determined.
			return true;
		}

		JavaTypeDesc<?> testedType = JavaTypeRegistry.getType(t);
		if (testedType.isAssignableFrom(this.getType())) {
			return true;
		}
		if (this.getType().isAssignableFrom(testedType)) {
			return true;
		}
		LOG.warn("Type {} is not applicable for property {} with current type {}", t.getName(), this.toString(), testedType.getType().getName());
		return false;
	}
	
	void setGetter(Method m) {
		if (!this.validateType(m.getReturnType())) {
			throw new BeanConfigurationException("Invalid getter type: " + m.getReturnType());
		}
		this.getter = m;
		this.propertyType = null;
	}
	
	void setSetter(Method m) {
		if (m.getGenericParameterTypes().length != 1) {
			throw new BeanConfigurationException("Setter with arguments count != 1 are not supported");
		}
		if (!this.validateType(m.getParameterTypes()[0])) {
			throw new BeanConfigurationException("Invalid setter type: " + m.getReturnType());
		}
		this.setter = m;
		this.propertyType = null;
	}
	
	void setField(Field f) {
		if (!this.validateType(f.getType())) {
			throw new BeanConfigurationException("Invalid field type: " + f.getType());
		}
		this.field = f;
		this.propertyType = null;
	}
	
	public BeanPropertyDesc getOverridedProperty() {
		if (this.overridedPropertySet) {
			return this.overridedProperty;
		}
		synchronized (this._lock) {
			if (this.overridedPropertySet) {
				return this.overridedProperty;
			}
			
			if (this.getBeanType().getSupertype() != null) {
				this.overridedProperty = this.getBeanType().getSupertype().getProperty(this.getName());
			}
			this.overridedPropertySet = true;
			
			return this.overridedProperty;
		}
	}
	
	/// end - Constructor and initialization
	public BeanTypeDesc<?> getBeanType() {
		return this.beanType;
	}
	
	/**
	 * Returns type of property getter – method or field will be used to get a value.
	 * @return {@link java.lang.reflect.Type} or null if getter wasn't set.
	 */
	public Type getRawGetterType() {
		AccessibleObject getter = this.getGetter();
		if (getter == null) {
			LOG.warn("Trying to get raw getter type for property {}. Getter not found in target type.", this.toString());
			return null;
		}
		if (getter instanceof Method) {
			return ((Method) getter).getGenericReturnType();
		} else if (getter instanceof Field) {
			return ((Field) getter).getGenericType();
		} else {
			LOG.warn("Trying to get raw getter type for property {}. Getter has unexpected type {}", this.toString(), getter.getClass().getName());
			return null;
		}
	}
	
	/**
	 * Returns type of property getter – method or field will be used to get a value.
	 * @return {@link java.lang.Class} or null if getter wasn't set.
	 */
	public Class<?> getRawGetterClass() {
		AccessibleObject getter = this.getGetter();
		if (getter == null) {
			LOG.warn("Trying to get raw getter class for property {}. Getter not found in target type.", this.toString());
			return null;
		}
		if (getter instanceof Method) {
			return ((Method) getter).getReturnType();
		} else if (getter instanceof Field) {
			return ((Field) getter).getType();
		} else {
			LOG.warn("Trying to get raw getter class for property {}. Getter has unexpected type {}", this.toString(), getter.getClass().getName());
			return null;
		}
	}
	
	/**
	 * Returns type of property setter – method or field will be used to get a value.
	 * @return {@link java.lang.reflect.Type} or null if setter wasn't set.
	 */
	public Type getRawSetterType() {
		AccessibleObject setter = this.getSetter();
		if (setter == null) {
			LOG.warn("Trying to get raw setter type for property {}. Setter not found in target type.", this.toString());
			return null;
		}
		if (setter instanceof Method) {
			return ((Method) setter).getGenericParameterTypes()[0];
		} else if (setter instanceof Field) {
			return ((Field) setter).getGenericType();
		} else {
			LOG.warn("Trying to get raw setter type for property {}. Setter has unexpected type {}", this.toString(), getter.getClass().getName());
			return null;
		}
	}
	
	/**
	 * Returns type of property setter – method or field will be used to get a value.
	 * @return {@link java.lang.Class} or null if setter wasn't set.
	 */
	public Class<?> getRawSetterClass() {
		AccessibleObject setter = this.getSetter();
		if (setter == null) {
			LOG.warn("Trying to get raw setter class for property {}. Setter not found in target type.", this.toString());
			return null;
		}
		if (setter instanceof Method) {
			return ((Method) setter).getParameterTypes()[0];
		} else if (setter instanceof Field) {
			return ((Field) setter).getType();
		} else {
			LOG.warn("Trying to get raw setter class for property {}. Setter has unexpected type {}", this.toString(), getter.getClass().getName());
			return null;
		}
	}
	
	protected AccessibleObject getGetter() {
		if (this.actualGetter != null) {
			return this.actualGetter;
		}
		if (this.field == null && this.getter == null) {
			return null;
		}
		synchronized (_lock) {
			this.actualGetter = PropertyUtils.select(this.field, this.getter, this.getBeanType().getMinFieldAccessLevel().getLevel(), this.getBeanType().getMinGetterAccessLevel().getLevel());
			if (this.actualGetter == null && this.getOverridedProperty() != null) {
				this.actualGetter = this.getOverridedProperty().getGetter();
			}
			return this.actualGetter;
		}
	}
	
	protected AccessibleObject getSetter() {
		if (this.actualSetter != null) {
			return this.actualSetter;
		}
	
		synchronized (_lock) {			
			this.actualSetter = PropertyUtils.select(this.field, this.setter, this.getBeanType().getMinFieldAccessLevel().getLevel(), this.getBeanType().getMinSetterAccessLevel().getLevel());
			if (this.actualSetter == null && this.getOverridedProperty() != null) {
				this.actualSetter = this.getOverridedProperty().getSetter();
			}
			return this.actualSetter;
		}
	}
	
	/**
	 * @return true if getter was set and can be used to retreive value.
	 */
	public boolean canGet() {
		if (this.getGetter() == null) {
			return false;
		}
		
		for (IBeanPropertyExtension be : this.extensions.values()) {
			if (!be.canGet()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return true if setter was set and can be used to set value.
	 */
	public boolean canSet() {
		if (this.getSetter() == null) {
			return false;
		}
		
		for (IBeanPropertyExtension be : this.extensions.values()) {
			if (!be.canSet()) {
				return false;
			}
		}
		return true;
	}
	
	public String getName() {
		return this.name;
	}

	public int getOrder() {
		return this.order;
	}
	
	public JavaTypeDesc<?> getType() {
		if (this.propertyType != null) {
			return this.propertyType;
		}
		synchronized (_lock) {
			if (this.propertyType != null) {
				return this.propertyType;
			}
			if (this.getOverridedProperty() != null) {
				this.propertyType = this.getOverridedProperty().getType();
				return this.propertyType;
			}
			Class<?> getterType = this.getRawGetterClass();
			Class<?> setterType = this.getRawSetterClass();
			
			JavaTypeDesc<?> ret = null;
			if (getterType != null) {
				ret = JavaTypeRegistry.getType(getterType);
			}
			if (setterType != null) {
				JavaTypeDesc<?> st = JavaTypeRegistry.getType(setterType);
				if (ret == null ||  (!ret.isAssignableFrom(st) && st.isAssignableFrom(ret))) {
					// Setter has more restricted type
					ret = st;
				}
			}

			this.propertyType = ret;
		}
		
		return this.propertyType;
	}

	public Object getValue(Object target) throws IllegalArgumentException, InvocationTargetException {
		if (target == null) {
			throw new NullPointerException("target is null");
		}

		JavaTypeDesc<?> targetTypeBase = JavaTypeRegistry.getType(target.getClass());
		if (!this.getBeanType().isAssignableFrom(targetTypeBase)) {
			return null;
		}
		
		BeanTypeDesc<?> targetType = (BeanTypeDesc<?>) targetTypeBase;
		
		// Property can be overrided. Finding real property.
		BeanPropertyDesc prop = targetType.getProperty(this.getName());
		if (prop == null) {
			LOG.error("Trying to get property value for property {} and object type {}. Property not found in target type", this.toString(), target.getClass().getName());
			return null;
		}

		AccessibleObject getter = prop.getGetter();
		if (getter == null) {
			LOG.warn("Trying to get property value for property {} and object type {}. Getter not found in target type", this.toString(), target.getClass().getName());
			return null;
		}
		boolean wasAccessible = getter.isAccessible();
		if (!wasAccessible) {
			getter.setAccessible(true);
		}
		try {
			if (getter instanceof Method) {
				try {
					return ((Method) getter).invoke(target);
				} catch (IllegalAccessException e) {
					LOG.error("Trying to get value of property {} and object type {}. Unknown problem with getter IllegalAccessException", this.toString(), target.getClass().getName());
					LOG.warn("Handled exception", e);
					return null;
				} catch ( IllegalArgumentException | InvocationTargetException e) {
					LOG.error("Trying to get value of property {} and object type {}. Unknown problem getter invocation", this.toString(), target.getClass().getName());
					LOG.warn("Handled exception", e);
					throw e;
				}
			} else if (getter instanceof Field) {
				try {
					return ((Field) getter).get(target);
				} catch (IllegalAccessException e) {
					LOG.error("Trying to get value of property {} and object type {}. Unknown problem with field's IllegalAccessException", this.toString(), target.getClass().getName());
					LOG.warn("Handled exception", e);
					return null;
				}
			} else {
				LOG.warn("Trying to get property value for property {} and object type {}. getter has unexpected type {}", this.toString(), target.getClass().getName(), getter.getClass().getName());
				return null;
			}
		} finally {
			if (!wasAccessible) {
				getter.setAccessible(false);
			}
		}
	}
	public boolean setValue(Object target, Object value) throws IllegalArgumentException, InvocationTargetException{
		if (target == null) {
			throw new NullPointerException("target is null");
		}
		
		JavaTypeDesc<?> targetTypeBase = JavaTypeRegistry.getType(target.getClass());
		if (!this.getBeanType().isAssignableFrom(targetTypeBase)) {
			return false;
		}
		
		BeanTypeDesc<?> targetType = (BeanTypeDesc<?>) targetTypeBase;
		BeanPropertyDesc prop = targetType.getProperty(this.getName());
		if (prop == null) {
			LOG.warn("Trying to set value of property {} and object type {}. Property not found in target type", this.toString(), target.getClass().getName());
			return false;
		}
		AccessibleObject setterObj = prop.getSetter();
		if (setterObj == null) {
			LOG.warn("Trying to set value of property {} and object type {}. Setter not found in target type", this.toString(), target.getClass().getName());
			return false;
		}
		
		boolean wasAccessible = setterObj.isAccessible();
		if (!wasAccessible) {
			setterObj.setAccessible(true);
		}
		try {
			if (setterObj instanceof Method) {
				try {
					((Method) setterObj).invoke(target, value);
					return true;
				} catch (IllegalAccessException e) {
					LOG.warn("Trying to set value of property {} and object type {}. Unknown problem with setter IllegalAccessException", this.toString(), target.getClass().getName());
					LOG.warn("Handled exception", e);
					return false;
				} catch ( IllegalArgumentException | InvocationTargetException e) {
					LOG.warn("Trying to set value of property {} and object type {}. Unknown problem with setter invocation", this.toString(), target.getClass().getName());
					LOG.warn("Handled exception", e);
					throw e;
				}
			} else if (setterObj instanceof Field) {
				try {
					((Field) setterObj).set(target, value);
					return true;
				} catch (IllegalAccessException e) {
					LOG.warn("Trying to set value of property {} and object type {}. Unknown problem with field's IllegalAccessException", this.toString(), target.getClass().getName());
					LOG.warn("Handled exception", e);
					return false;
				}
			} else {
				LOG.warn("Trying to set value of property {} and object type {}. setter has unexpected type {}", this.toString(), target.getClass().getName(), getter.getClass().getName());
				return false;
			}
		} finally {
			if (!wasAccessible) {
				setterObj.setAccessible(false);
			}
		}
	}
	public List<Annotation> getAnnotatedAnnotations(Class<? extends Annotation> atype) {
		ArrayList<Annotation> ret = new ArrayList<>();
		if (this.field != null) {
			for (Annotation a : this.field.getAnnotations()) {
				Class<? extends Annotation> aclass = a.annotationType();
				if (aclass.getAnnotation(atype) != null) { 
					ret.add(a);
				}
			}
		}
		if (this.getter != null) {
			for (Annotation a : this.getter.getAnnotations()) {
				if (a.getClass().getAnnotation(atype) != null) { 
					ret.add(a);
				}
			}
		}
		if (this.setter != null) {
			for (Annotation a : this.setter.getAnnotations()) {
				if (a.getClass().getAnnotation(atype) != null) { 
					ret.add(a);
				}
			}
		}
		
		return ret;
	}
	public <T extends Annotation> T getAnnotation(Class<T> type) {
		T ret = null;
		
		boolean fldChecked = false;
		boolean getChecked = false;
		boolean setChecked = false;
		
		AccessibleObject currentGetter = this.getGetter();
		if (currentGetter != null) {
			ret = currentGetter.getAnnotation(type);
			if (ret != null) {
				return ret;
			}
			if (currentGetter instanceof Method) {
				getChecked = true;
			} else {
				fldChecked = true;
			}
		}
		
		AccessibleObject currentSetter = this.getSetter();
		if (currentSetter != null) {
			ret = currentSetter.getAnnotation(type);
			if (ret != null) {
				return ret;
			}
			if (currentSetter instanceof Method) {
				setChecked = true;
			} else {
				fldChecked = true;
			}
		}
		
		// Looking everywhere.
		if (this.field != null && !fldChecked) {
			ret = this.field.getAnnotation(type);
			if (ret != null) {
				return ret;
			}
		}
		
		if (this.getter != null && !getChecked) {
			ret = this.getter.getAnnotation(type);
			if (ret != null) {
				return ret;
			}
		}
		if (this.setter != null && !setChecked) {
			ret = this.setter.getAnnotation(type);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends IBeanPropertyExtension> E getExtension(Class<E> type) {
		return (E) this.extensions.get(type);
	}

	public void registerExtension(IBeanPropertyExtension extension) {
		if (extension == null) {
			throw new NullPointerException("propertyExt is null");
		}
		
		if (this.extensions.containsKey(extension.getClass())) {
			throw new BeanConfigurationException("Extension with type " + extension.getClass().getName() + " already registered");
		}
		this.extensions.put(extension.getClass(), extension);
	}
	
	@Override
	public String toString() {
		return "BeanProperty {" + this.getBeanType().getFullName() + " : " + this.getName() + "}";
	}
} 
