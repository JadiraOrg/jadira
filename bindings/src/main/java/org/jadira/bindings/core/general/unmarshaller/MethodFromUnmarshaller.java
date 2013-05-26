/*
 *  Copyright 2010, 2011 Chris Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distringibuted under the License is distringibuted on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.bindings.core.general.unmarshaller;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.jadira.bindings.core.api.BindingException;
import org.jadira.bindings.core.api.FromUnmarshaller;

/**
 * Binding that supports an unmarshal method. The
 * unmarshal method must be statically scoped. It must accept a single parameter
 * of type S and return a type of T. For example: </p>
 * <p>
 * {@code public static BoundType unmarshal(String string)}
 * </p>
 * @param <S> Source type for the conversion
 * @param <T> Target type
 */
public final class MethodFromUnmarshaller<S, T> implements FromUnmarshaller<S, T> {

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	
    private final MethodHandle unmarshalHandle;

    private final Class<S> boundClass;

    private final Class<T> targetClass;
    
    /**
     * Create a new instance
     * @param boundClass Bound class
     * @param unmarshal Unmarshal method on the target class
     */
    public MethodFromUnmarshaller(Class<S> boundClass, Method unmarshal) {

        this.boundClass = boundClass;
        
        if (unmarshal.getParameterTypes().length != 1) {
            throw new IllegalStateException("unmarshal method must define a single parameter");
        }
        if (!Modifier.isStatic(unmarshal.getModifiers())) {
            throw new IllegalStateException("unmarshal method must be defined as static");
        }
        if (!boundClass.isAssignableFrom(unmarshal.getReturnType())) {
            throw new IllegalStateException("unmarshal method must return " + boundClass.getSimpleName());
        }

        try {
			this.unmarshalHandle = LOOKUP.unreflect(unmarshal);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Method is not accessible" + unmarshal);
		}
        
        @SuppressWarnings("unchecked")
        Class<T> myTarget = (Class<T>)unmarshal.getParameterTypes()[0];
        this.targetClass = myTarget;
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public S unmarshal(T object) {

    	if (object != null && !targetClass.isAssignableFrom(object.getClass())) {
    		throw new IllegalArgumentException("Supplied object was not instance of target class");
    	}
    	
        try {
            return getBoundClass().cast(unmarshalHandle.invoke(object));
        } catch (Throwable ex) {
        	if (ex.getCause() instanceof RuntimeException) {
                throw (RuntimeException) ex.getCause();
            }
            throw new BindingException(ex.getMessage(), ex.getCause());
        }
    }
    
	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public Class<S> getBoundClass() {
        return boundClass;
    }
    
	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public Class<T> getTargetClass() {
        return targetClass;
    }
}
