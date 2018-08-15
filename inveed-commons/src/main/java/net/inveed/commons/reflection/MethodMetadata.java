package net.inveed.commons.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.inveed.commons.reflection.ext.IMethodExtension;

public final class MethodMetadata {

	private static final Logger LOG = LoggerFactory.getLogger(MethodMetadata.class);
	
	private final HashMap<Class<? extends IMethodExtension>, IMethodExtension> extensions = new HashMap<>();
	
    private final String name;
    private final Method method;
    
    private String asString;
    
    private final List<ParameterMetadata> params;

    MethodMetadata( String name,  Method method,
    		List<ParameterMetadata> params) {
    	
        this.name = name;
        this.method = method;
        this.params = params;
    }

    
    public String getName() {
        return name;
    }

    
    public Method getMethod() {
        return method;
    }
    
    public String getSignature() {
    	StringBuilder sb = new StringBuilder();
        sb.append(this.getMethod().getReturnType().getName());
        sb.append("###");
        sb.append(this.getMethod().getName());
        sb.append("###(");
        for (Class<?> type : this.getMethod().getParameterTypes()) {
        	sb.append(type.getName());
        	sb.append("!!");
        }
        sb.append(')');
        return sb.toString();
    }

    
    public List<ParameterMetadata> getParams() {
        return params;
    }
    
    public <A extends Annotation> A getAnnotation(Class<A> type) {
    	return this.method.getAnnotation(type);
    }
    
    @SuppressWarnings("unchecked")
	public <E extends IMethodExtension> E getExtension(Class<E> type) {
		return (E) this.extensions.get(type);
	}

	public void registerExtension(IMethodExtension ext) {
		if (this.extensions.containsKey(ext.getClass())) {
			LOG.error("Trying to register extension with type {} twice. ", ext.getClass());
			return;
		}
		this.extensions.put(ext.getClass(), ext);
	}
	
	@Override
	public String toString() {
		if (this.asString != null) {
			return this.asString;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(this.getName());
		sb.append('[');
		for (ParameterMetadata pm : this.getParams()) {
			sb.append(pm.toString());
			sb.append(',');
		}
		sb.replace(sb.length()-1, sb.length(), "]");
		
		this.asString = sb.toString();
		return this.asString;
	}
}
