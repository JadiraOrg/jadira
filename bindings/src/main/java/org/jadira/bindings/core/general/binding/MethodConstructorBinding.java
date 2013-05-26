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
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.bindings.core.general.binding;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.api.BindingException;
import org.jadira.bindings.core.general.marshaller.MethodToMarshaller;

/**
 * Binding that supports a to contract, and a constructor parameterised by a S
 * @param <S> Source type for the conversion
 * @param <T> Target type
 */
public final class MethodConstructorBinding<S, T> extends MethodToMarshaller<S, T> implements Binding<S, T> {

    private final Constructor<S> unmarshal;

    /**
     * Constructs a binding that supports a marshal method and an unmarshalling constructor
     * @param unmarshal The Constructor
     * @param targetClass The target class for marshalling to
     * @param marshal The marshalling method
     */
    public MethodConstructorBinding(Constructor<S> unmarshal, Class<T> targetClass, Method marshal) {
        
        super(unmarshal.getDeclaringClass(), targetClass, marshal);
        
        if (getBoundClass().isInterface() 
                || Modifier.isAbstract(getBoundClass().getModifiers()) 
                || getBoundClass().isLocalClass() 
                || getBoundClass().isMemberClass()) {
            throw new IllegalStateException("unmarshal constructor must have an instantiable target class");
        }
        
        this.unmarshal = unmarshal;
    }

    /**
     * @{inheritDoc}
     */
    /* @Override */
    public S unmarshal(T str) {
        try {
            return unmarshal.newInstance(str);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Constructor is not accessible");
        } catch (InstantiationException ex) {
            throw new IllegalStateException("Constructor is not valid");
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof RuntimeException) {
                throw (RuntimeException) ex.getCause();
            }
            throw new BindingException(ex.getMessage(), ex.getCause());
        }
    }
}
