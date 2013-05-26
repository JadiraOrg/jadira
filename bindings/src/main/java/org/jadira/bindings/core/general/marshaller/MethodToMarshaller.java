/*
 *  Copyright 2011 Chris Pheby
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
package org.jadira.bindings.core.general.marshaller;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.jadira.bindings.core.api.BindingException;
import org.jadira.bindings.core.api.ToMarshaller;

/**
 * Base class providing capability to perform marshalling of source object type
 * to target. This class uses reflection.
 * <p>
 * The marshal method must either
 * </p>
 * <p>
 * a) be instance scoped and defined as part of class S. It must accept no
 * parameters and return a type of T. For example:
 * </p>
 * <p>
 * {@code public String marshal()}
 * </p>
 * <p>
 * b) be statically scoped. It must accept a single parameter of type S and
 * return a type of T. For example:
 * </p>
 * <p>
 * {@code public static String marshal(BoundType param)}
 * </p>
 * @param <S> Source type for the conversion
 * @param <T> Source type
 */
public class MethodToMarshaller<S, T> implements ToMarshaller<S, T> {

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	
    private final Class<S> boundClass;

    private final Class<T> targetClass;
    
    private final MethodHandle marshalHandle;

    /**
     * Create a new instance
     * @param boundClass Bound class
     * @param targetClass Destination class
     * @param marshal Marshal instance method on the target class
     */
    public MethodToMarshaller(Class<S> boundClass, Class<T> targetClass, Method marshal) {
        
        if (marshal.getParameterTypes().length == 0 && Modifier.isStatic(marshal.getModifiers())) {
            throw new IllegalStateException("marshal method must either be instance scope or define a single parameter");
        } else if (marshal.getParameterTypes().length == 1 && (!Modifier.isStatic(marshal.getModifiers()))) {
            throw new IllegalStateException("marshal method must either be instance scope or define a single parameter");
        } else if (marshal.getParameterTypes().length >= 2) {
            throw new IllegalStateException("marshal method must either be instance scope or define a single parameter");
        }
        
        if (!targetClass.isAssignableFrom(marshal.getReturnType())) {
            throw new IllegalStateException("marshal method must return an instance of target class");
        }
        if (!marshal.getDeclaringClass().isAssignableFrom(boundClass) && !Modifier.isStatic(marshal.getModifiers())) {
            throw new IllegalStateException("marshal method must be defined as part of " + boundClass.getSimpleName());
        }

        this.boundClass = boundClass;
        this.targetClass = targetClass;

        try {
			this.marshalHandle = LOOKUP.unreflect(marshal);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Method is not accessible" + marshal);
		}

    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public T marshal(S object) {

        try {
        	final T result = (T) marshalHandle.invoke(object);
            return result;
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
