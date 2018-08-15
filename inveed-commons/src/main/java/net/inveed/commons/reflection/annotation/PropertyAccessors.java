package net.inveed.commons.reflection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.commons.reflection.AccessLevel;

/**
 * Annotation allows to filter some methods or properties with low access level while scan
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PropertyAccessors {
	AccessLevel minimumFieldAccessLevel() default AccessLevel.PUBLIC;
	AccessLevel minimumGetterAccessLevel() default AccessLevel.PUBLIC;
	AccessLevel minimumSetterAccessLevel() default AccessLevel.PUBLIC;
}
