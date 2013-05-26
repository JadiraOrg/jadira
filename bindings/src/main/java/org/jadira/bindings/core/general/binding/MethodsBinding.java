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
package org.jadira.bindings.core.general.binding;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.api.BindingException;
import org.jadira.bindings.core.general.marshaller.MethodToMarshaller;

/**
 * Binding that supports a marshal contract, and a unmarshal method. The
 * unmarshal method must be statically scoped. It must accept a single parameter
 * of type S and return a type of T. For example: </p>
 * <p>
 * {@code public static BoundType unmarshal(String string)}
 * </p>
 * @param <S> Source type for the conversion
 * @param <T> Target type
 */
public final class MethodsBinding<S, T> extends MethodToMarshaller<S, T> implements Binding<S, T> {

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	
    private final MethodHandle unmarshalHandle;

    /**
     * Constructs a binding that supports a marshal method and an unmarshal method
     * @param marshal The marshalling method
     * @param unmarshal The unmarshalling method
     * @param boundClass The source class for unmarshalling from
     * @param targetClass The target class for marshalling to
     */
    public MethodsBinding(Method marshal, Method unmarshal, Class<S> boundClass, Class<T> targetClass) {
        
    	super(boundClass, targetClass, marshal);

        if (unmarshal.getParameterTypes().length != 1) {
            throw new IllegalStateException("unmarshal method must define a single parameter");
        }
        if (!Modifier.isStatic(unmarshal.getModifiers())) {
            throw new IllegalStateException("unmarshal method must be defined as static");
        }

        if (unmarshal.getParameterTypes()[0] != targetClass) {
            throw new IllegalStateException("unmarshal method must be parameterized by " + targetClass.getSimpleName());
        }
        if (!boundClass.isAssignableFrom(unmarshal.getReturnType())) {
            throw new IllegalStateException("unmarshal method must return " + boundClass.getSimpleName());
        }
        
        try {
			this.unmarshalHandle = LOOKUP.unreflect(unmarshal);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Method is not accessible" + unmarshal);
		}
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public S unmarshal(T string) {

        try {
            return getBoundClass().cast(unmarshalHandle.invoke(string));
        } catch (Throwable ex) {
        	if (ex.getCause() instanceof RuntimeException) {
                throw (RuntimeException) ex.getCause();
            }
            throw new BindingException(ex.getMessage(), ex.getCause());
        }
    }
}
