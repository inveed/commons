package net.inveed.commons.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.inveed.commons.reflection.ext.IParameterExtension;

/**
 * Date: 8/1/14
 * Time: 7:44 PM
 * Method parameter metadata
 *
 * @author Artem Prigoda
 */
public final class ParameterMetadata {

	private static final Logger LOG = LoggerFactory.getLogger(ParameterMetadata.class);
	private final String name;
    /**
     * Actual java type
     */
    
    private final Class<?> type;

    /**
     * Generic java type
     */
    
    private final Type genericType;
    
    private final Annotation[] annotations;

    /**
     * Index in method arguments
     */
    private final int index;
	private final HashMap<Class<? extends IParameterExtension>, IParameterExtension> extensions = new HashMap<>();

    ParameterMetadata(String name, Class<?> type, Type genericType, int index, Annotation[] annotations) {
    	this.name = name;
        this.type = type;
        this.genericType = genericType;
        this.index = index;
        this.annotations = annotations;
    }
    
    public <A extends Annotation> A getAnnotation(Class<A> type) {
    		return TypeUtils.getAnnotation(this.annotations, type);
    }
    
    public String getName() {
    	return this.name;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public int getIndex() {
        return index;
    }

    
    public Type getGenericType() {
        return genericType;
    }
    

	@SuppressWarnings("unchecked")
	public <E extends IParameterExtension> E getExtension(Class<E> type) {
		return (E) this.extensions.get(type);
	}

	public void registerExtension(IParameterExtension ext) {
		if (this.extensions.containsKey(ext.getClass())) {
			LOG.error("Trying to register extension with type {} twice. ", ext.getClass());
			return;
		}
		this.extensions.put(ext.getClass(), ext);
	}
	
	@Override
	public String toString() {
		if (this.getName() == null)
			return this.type.toString();
		else {
			return this.getName() + "(" + this.type.toString() + ")";
		}
	}
}
