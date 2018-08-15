package net.inveed.commons.reflection.ext;

import net.inveed.commons.reflection.MethodMetadata;

public interface IMethodFilter {

	boolean isValid(MethodMetadata mm);

}
