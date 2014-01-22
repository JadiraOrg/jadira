/*
 *  Copyright 2013 Chris Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.reflection.cloning.implementor.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

import org.jadira.reflection.cloning.api.CloneDriver;
import org.jadira.reflection.cloning.api.CloneImplementor;

/**
 * A Clone Implementor that uses a copy constructor on the target class to perform the clone.
 */
public final class CopyConstructorImplementor implements CloneImplementor {

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	
    private final Class<?> boundClass;

	private final MethodHandle constructHandle;

    /**
     * Create a new instance
     * @param constructor Constructor to be used
     */
    public CopyConstructorImplementor(Constructor<?> constructor) {
        
        this.boundClass = constructor.getDeclaringClass();
        
        if (boundClass.isInterface() 
                || Modifier.isAbstract(boundClass.getModifiers()) 
                || boundClass.isLocalClass() 
                || boundClass.isMemberClass()) {
            throw new IllegalStateException("constructor constructor must have an instantiable target class");
        }
        
        if (constructor.getParameterTypes().length != 1) {
        	throw new IllegalStateException("constructor constructor must have a single parameter");
        }
        
		try {
			constructor.setAccessible(true);
			this.constructHandle = LOOKUP.unreflectConstructor(constructor);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Method is not accessible" + constructor);
		}

    }

	@Override
    public <T> T clone(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse, long stackDepth) {
    	
    	if (obj != null && !(boundClass.equals(obj))) {
    		throw new IllegalArgumentException("Supplied object was not instance of class: " + boundClass.getName());
    	}
    	
        try {
            final T myResult = (T)constructHandle.invoke(obj);
            return myResult;
        } catch (Throwable ex) {
            if (ex.getCause() instanceof RuntimeException) {
                throw (RuntimeException) ex.getCause();
            }
            throw new IllegalStateException("Constructor could not be invoked", ex.getCause());
        }
    }
	
    @Override
    public <T> T newInstance(Class<T> c) {
        throw new UnsupportedOperationException("newInstance() is unsupported");
    }

    @Override
    public boolean canClone(Class<?> clazz) {
        return boundClass.equals(clazz);
    }
}
