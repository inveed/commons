package net.inveed.commons.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.inveed.commons.reflection.annotation.ParameterName;
import net.inveed.commons.reflection.annotation.PropertyAccessors;
import net.inveed.commons.reflection.annotation.PropertyIgnore;
import net.inveed.commons.reflection.ext.IMethodFilter;

public class BeanTypeDesc<T> extends JavaTypeDesc<T> {
	private static final Logger LOG = LoggerFactory.getLogger(BeanTypeDesc.class);
	private static final String GET_PREFIX = "get";
	private static final String IS_PREFIX = "is";
	private static final String SET_PREFIX = "set";

	private final Object _lock = new Object();
	
	private final Class<T> type;
	private final Constructor<?> defaultConstructor;
	private final List<Constructor<?>> constructors = new ArrayList<>();
	
	private List<BeanPropertyDesc> allProperies;
	private LinkedHashMap<String, BeanPropertyDesc> __properties;
	private LinkedHashMap<String, List<MethodMetadata>> __methods;
	
	private AccessLevel minGetterAccessLevel;
	private AccessLevel minSetterAccessLevel;
	private AccessLevel minFieldAccessLevel;
	

	/// INITIALIZATION
	
	protected BeanTypeDesc(Class<T> type) {
		LOG.debug("Creating new type description for type {}", type);
		if (type == null) {
			throw new NullPointerException("type is null");
		}
		
		this.type = type;
		
		Constructor<?> dc = null;
		
		try {
			try{
				dc = type.getConstructor();
				LOG.debug("Found default public constructor");
			} catch (NoSuchMethodException e) {
			}
			
			Constructor<?>[] ctors = this.getType().getDeclaredConstructors();
			
			for (int i = 0; i < ctors.length; i++) {
			    Constructor<?> ctor = ctors[i];
			    if (ctor.getGenericParameterTypes().length == 0 && dc != null) {
			    	dc = ctor;
			    	LOG.debug("Found default non-public constructor");
			    	break;
			    } else if (ctor.getGenericParameterTypes().length > 0) {
			    	constructors.add(ctor);
			    }
			}
		} catch (SecurityException e) {
			LOG.warn("Cannot find default constructor for type " + this.getFullName(), e);
		}
		
		this.defaultConstructor = dc;
		if (this.defaultConstructor != null) {
			this.defaultConstructor.setAccessible(true);
		}
		
		PropertyAccessors paa = type.getAnnotation(PropertyAccessors.class);
		if (paa != null) {
			this.minFieldAccessLevel = paa.minimumFieldAccessLevel();
			this.minGetterAccessLevel = paa.minimumGetterAccessLevel();
			this.minSetterAccessLevel = paa.minimumSetterAccessLevel();
		} else {
			this.minFieldAccessLevel = AccessLevel.PUBLIC;
			this.minGetterAccessLevel = AccessLevel.PUBLIC;
			this.minSetterAccessLevel = AccessLevel.PUBLIC;
		}
	}
		
	protected BeanPropertyDesc createProperty(String name) {
		return new BeanPropertyDesc(name, this);
	}
	
	@Override
	protected void initialize() {
		if (this.isInitialized()) {
			return;
		}
		synchronized (this._lock) {
			if (this.isInitialized()) {
				return;
			}
			LOG.debug("Initializing type description {}", this.getFullName());
			
			this.findProperties();
			this.findMethods();
			super.initialize();
		}
	}
	
	protected void findMethods() {
		LOG.debug("Searching for available methods");
		
		Method methodList[] = this.type.getDeclaredMethods();
		this.__methods = new LinkedHashMap<>();
		
		// Now analyze each method.
		for (int i = 0; i < methodList.length; i++) {
			Method method = methodList[i];
			if (method == null) {
				continue;
			}

			// skip static methods.
			int mods = method.getModifiers();
			if (Modifier.isStatic(mods)) {
				continue;
			}

			String name = method.getName();
			LOG.debug("Analyzing method '{}'", name );
			
			Annotation[][] allParametersAnnotations = method.getParameterAnnotations();
			int methodParamsSize = allParametersAnnotations.length;
			Class<?>[] parameterTypes = method.getParameterTypes();
			Type[] genericParameterTypes = method.getGenericParameterTypes();

			ArrayList<ParameterMetadata> parametersMetadata = new ArrayList<>();
			for (int pi = 0; pi < methodParamsSize; pi++) {
				ParameterName pna = TypeUtils.getAnnotation(allParametersAnnotations[pi], ParameterName.class);
				String pname = null;
				if (pna != null) {
					pname = pna.value();
				}
				parametersMetadata.add(new ParameterMetadata(pname, parameterTypes[pi], genericParameterTypes[pi], pi, allParametersAnnotations[pi]));
			}
			
			MethodMetadata mm = new MethodMetadata(name, method, parametersMetadata);

			List<MethodMetadata> listByName = this.__methods.get(name);
			if (listByName == null) {
				listByName = new ArrayList<>();
				this.__methods.put(name, listByName);
			}
			
			listByName.add(mm);
		}
	}

	protected void findProperties() {
		LOG.debug("Searching for properties");
		
		HashMap<String, BeanPropertyDesc> propertiesByNativeNames = new HashMap<>();
		
        Method methodList[] = this.type.getDeclaredMethods();
        Field fieldsList[] = this.type.getDeclaredFields();
        
        LOG.debug("Analyzing getter/setter methods");
        for (int i = 0; i < methodList.length; i++) {
        	
            Method method = methodList[i];
            if (method == null) {
                continue;
            }
            
            // skip static methods.
            int mods = method.getModifiers();
            if (Modifier.isStatic(mods)) {
                continue;
            }
            
            if (method.getAnnotation(PropertyIgnore.class) != null) {
            	LOG.debug("Skipping method {} because of PropertyIgnore annotation", method.toString());
            	continue;
            }
            
            String name = method.getName();
            
            Class<?>[] argTypes = method.getParameterTypes();
            Class<?> resultType = method.getReturnType();
            int argCount = argTypes.length;
          

            if (name.length() <= 3 && !name.startsWith(IS_PREFIX)) {
                LOG.debug("Skipping method {} because of too short name", method.toString());
                continue;
            }
            
            // Finding getters and setters according to generic rules.

            if (argCount == 0) {
                if (name.startsWith(GET_PREFIX)) {
                	String propname = PropertyUtils.normalizePropertyName(name.substring(3));
                	LOG.debug("Using method {} as simple getter for property {}", method.toString(), propname);
                	BeanPropertyDesc pd = propertiesByNativeNames.get(propname);
                    if (pd == null) {
                    	pd = this.createProperty(propname);
                    	propertiesByNativeNames.put(propname, pd);
                    }
                    pd.setGetter(method);
                } else if (resultType == boolean.class && name.startsWith(IS_PREFIX)) {
                	String propname = PropertyUtils.normalizePropertyName(name.substring(2));
                	LOG.debug("Using method {} as simple boolean getter for property {}", method.toString(), propname);
                	BeanPropertyDesc pd = propertiesByNativeNames.get(propname);
                    if (pd == null) {
                    	pd = this.createProperty(propname);
                    	propertiesByNativeNames.put(propname, pd);
                    }
                    pd.setGetter(method);
                } else {
                	LOG.debug("Skipping method {}", method.toString());
                }
            } else if (argCount == 1) {
                if (int.class.equals(argTypes[0]) && name.startsWith(GET_PREFIX)) {
                	LOG.warn("Skipping method {} as simple index getter - NOT SUPPORTED", method.toString());
                } else if (void.class.equals(resultType) && name.startsWith(SET_PREFIX)) {
                	String propname = PropertyUtils.normalizePropertyName(name.substring(3));
                	LOG.debug("Using method {} as simple setter for property {}", method.toString(), propname);
                	BeanPropertyDesc pd = propertiesByNativeNames.get(propname);
                    if (pd == null) {
                    	pd = this.createProperty(propname);
                    	propertiesByNativeNames.put(propname, pd);
                    }
                    pd.setSetter(method);
                } else {
                	LOG.debug("Skipping method {}", method.toString());
                }
            } else if (argCount == 2) {
            	if (name.startsWith(SET_PREFIX)) {
            		LOG.warn("Skipping method {} as simple index setter - NOT SUPPORTED", method.toString());
            	} else {
                	LOG.debug("Skipping method {}", method.toString());
                }
            } else {
            	LOG.debug("Skipping method {}", method.toString());
            }
        }
        
        LOG.debug("Analyzing fields");
        
        for (int i = 0; i < fieldsList.length; i++) {
            Field field = fieldsList[i];
            
            if (Modifier.isStatic(field.getModifiers())) {
            	continue;
            }
            
            if (field.getAnnotation(PropertyIgnore.class) != null) {
            	LOG.debug("Skipping field {} because of PropertyIgnore annotation", field.toString());
            	continue;
            }
            
            String name = field.getName();
            
            LOG.debug("Using field {} for property {}", field.toString(), name);
            BeanPropertyDesc pd = propertiesByNativeNames.get(name);
            while (pd == null && name.startsWith("_")) {
            	name = name.substring(1);
            	pd = propertiesByNativeNames.get(name);
            }
            
            if (pd == null) {
            	pd = this.createProperty(name);
            }
            pd.setField(field);
        }
        
        // Отсортируем свойства по индексу
        
        LOG.debug("Sorting fields");
        
        ArrayList<BeanPropertyDesc> propertiesList = new ArrayList<>(propertiesByNativeNames.values());
        Collections.sort(propertiesList, new Comparator<BeanPropertyDesc>() {

			@Override
			public int compare(BeanPropertyDesc f1, BeanPropertyDesc f2) {
				if (f1 == null && f2 == null) 
					return 0;
				else if (f1 == null)
					return 1;
				else if (f2 == null)
					return -1;
				else if (f1.getOrder() == f2.getOrder()) {
					return f1.getName().compareTo(f2.getName());
				} else if (f1.getOrder() < f2.getOrder())
					return -1;
				else return 1;
			}} );
        
        LinkedHashMap<String, BeanPropertyDesc> ret = new LinkedHashMap<>();
        for (BeanPropertyDesc pd : propertiesList)  {
        	ret.put(pd.getName(), pd);
        }
        
        this.__properties = ret;
	}
	
	@Override
	public String toString() {
		return "BeanTypeDesc {" + this.getType() + "}";
	}
	
	public <A extends Annotation> A getAnnotation(Class<A> type) {
		return this.type.getAnnotation(type);
	}
	
	public <A extends Annotation> A getDeclaredAnnotation(Class<A> type) {
		return this.type.getDeclaredAnnotation(type);
	}
	
	public LinkedHashMap<String, BeanPropertyDesc> getDeclaredProperties() {
		if (this.__properties != null) {
			return this.__properties;
		}
		this.initialize();
		return this.__properties;
	}
	
	public LinkedHashMap<String, List<MethodMetadata>> getDeclaredMethods() {
		if (this.__methods != null) {
			return this.__methods;
		}
		this.initialize();
		return this.__methods;
	}
	
	public List<MethodMetadata> getMethods(String name, IMethodFilter filter) {
		HashMap<String,MethodMetadata> ret = new HashMap<>();
		this.fillMethods(name, filter, ret);
		return new ArrayList<>(ret.values());
	}
	
	public List<MethodMetadata> getMethods(String name) {
		return this.getMethods(name, null);
	}

	public List<BeanPropertyDesc> getProperties() {
		if (this.allProperies != null) {
			return this.allProperies;
		}
		
		synchronized (this._lock) {
			if (this.allProperies != null) {
				return this.allProperies;
			}
			LOG.debug("Building list of all properties for bean description {}", this.getFullName());
			
			HashMap<String, BeanPropertyDesc> ret = new HashMap<>();
			BeanTypeDesc<?> current = this;
			while (current != null) {
				for (BeanPropertyDesc bpd : current.getDeclaredProperties().values()) {
					if (!ret.containsKey(bpd.getName())) {
						ret.put(bpd.getName(), bpd);
					}
				}
				current = current.getSupertype();
			}
			this.allProperies = new ArrayList<>(ret.values());
			return this.allProperies;
		}
	}
	
	public BeanPropertyDesc getDeclaredProperty(String normalizedName) {
		return this.getDeclaredProperties().get(normalizedName);
	}
	
	public List<MethodMetadata> getDeclaredMethods(String name) {
		return Collections.unmodifiableList(this.getDeclaredMethods().get(name));
	}
	public BeanPropertyDesc getProperty(String name) {
		BeanPropertyDesc ret = this.getDeclaredProperty(name);
		if (ret == null && this.getSupertype() != null) {
			ret = this.getSupertype().getProperty(name);
		}
		return ret;
	}

	public String getFullName() {
		return this.type.getName();
	}
	
	public Class<T> getType() {
		return type;
	}
	
	public Constructor<?> getDefaultConstructor() {
		return this.defaultConstructor;
	}
	
	public BeanTypeDesc<?> getSupertype() {
		if (this.type.getSuperclass() == Object.class) {
			return null;
		}
		JavaTypeDesc<?> ret = JavaTypeRegistry.getType(this.type.getSuperclass());
		if (ret instanceof BeanTypeDesc) {
			return (BeanTypeDesc<?>) ret;
		}
		return null;
	}
	
	public String getShortName() {
		return this.type.getSimpleName();
	}
	
	AccessLevel getMinGetterAccessLevel() {
		return minGetterAccessLevel;
	}

	AccessLevel getMinSetterAccessLevel() {
		return minSetterAccessLevel;
	}

	AccessLevel getMinFieldAccessLevel() {
		return minFieldAccessLevel;
	}

	/// INSTANTIATION
	
	public Object newInstance() {
		if (this.defaultConstructor == null)
			return null;
		
		try {
			return this.defaultConstructor.newInstance();
		} catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			LOG.warn("Cannot create new instance with default constructor for type " + this.getFullName(), e);
			return null;
		}
	}	
	
	/*
	public Object newInstance(Object[] arguments, String[] fieldNameHints) throws InstantiationException, InvocationTargetException {
		HashMap<Constructor<?>, ArgumentMatch> supportedConstructors = new HashMap<>();
		
		for (Constructor<?> c : this.constructors) {
			if (c.getParameters().length != arguments.length) {
				continue;
			}
			
			ArgumentMatch match = TypeUtils.getArgumentMatches(arguments, c.getParameters(), fieldNameHints);
			if (match != null) {
				supportedConstructors.put(c, match);
			}
		}
		
		Constructor<?> selectedConstructor = null;
		int matchScore = 0;
		for (Constructor<?> c : supportedConstructors.keySet()) {
			ArgumentMatch l = supportedConstructors.get(c);
			if (l == null) {
				continue;
			}
			
			int score = l.getScore();
			if (score > matchScore) {
				matchScore = score;
				selectedConstructor = c;
			}
		}
		
		if (selectedConstructor != null) {
			ArgumentMatch match = supportedConstructors.get(selectedConstructor);
			Object[] args = match.getSorted();
			selectedConstructor.setAccessible(true);
			try {
				return selectedConstructor.newInstance(args);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				return null;
			}
		}
		
		for (String s: fieldNameHints) {
			if (s == null) {
				return null; // для маппинга по полям должны быть заданы все хинты
			}
		}
		Object ret = this.newInstance();
		if (ret == null) {
			return null;
		}
		
		for (int i = 0; i < fieldNameHints.length; i++) {
			BeanPropertyDesc fld = this.getProperty(fieldNameHints[i]);
			if (fld == null) {
				return null;
			}
			Object fv = arguments[i];
			if (fv == null) {
				if (fld.getType().getType().isPrimitive()) {
					return null;
				} else {
					fld.setValue(ret, null);
				}
			} else {
				JavaTypeDesc<?> fvType = JavaTypeRegistry.getType(fv.getClass());
				if (fld.getType().isAssignableFrom(fvType)) {
					fld.setValue(ret, arguments[i]);
				} else {
					Object conv = TypeUtils.toObject(fld.getType(), fv);
					if (conv == null) {
						return null;
					}
					if (fld.getType().isAssignableFrom(fvType)) {
						fld.setValue(ret, conv);
					} else {
						return null;
					}
				}
			}
			
		}
		
		return ret;
	}
*/
	/// HELPERS
	
	protected void fillMethods(String name, IMethodFilter filter, HashMap<String, MethodMetadata> map) {
		List<MethodMetadata> l = this.getDeclaredMethods().get(name);
		
		if (l != null) {
			for (MethodMetadata mm : l) {
				String signature = mm.getSignature();
				if (map.containsKey(signature)) {
					continue;
				}
				if (filter != null && !filter.isValid(mm)) {
					continue;
				}
				map.put(signature, mm);
			}
		}
		
		if (this.getSupertype() != null) {
			this.getSupertype().fillMethods(name, filter, map);
		}
	}
	
	

	
}
