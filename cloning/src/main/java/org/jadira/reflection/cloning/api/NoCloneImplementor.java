package org.jadira.reflection.cloning.api;

import java.util.IdentityHashMap;

/**
 * This class is a placeholder to indicate the lack of CloneImplementor capability.
 * It is default CloneImplementor implementation for @Cloneable instances 
 */
public class NoCloneImplementor implements CloneImplementor {

	@Override
	public <T> T newInstance(Class<T> c) {
		throw new UnsupportedOperationException("Should not be invoked");
	}

	@Override
	public boolean canClone(Class<?> clazz) {
		throw new UnsupportedOperationException("Should not be invoked");
	}

	@Override
	public <T> T clone(T obj, CloneDriver context,
			IdentityHashMap<Object, Object> referencesToReuse, long stackDepth) {
		throw new UnsupportedOperationException("Should not be invoked");
	}
}
