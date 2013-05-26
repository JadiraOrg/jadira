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
package org.jadira.bindings.core.general.unmarshaller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.jadira.bindings.core.api.BindingException;
import org.jadira.bindings.core.api.FromUnmarshaller;

/**
 * Unmarshaller supports constructor parameterised by a target object
 * @param <S> Source type for the conversion
 * @param <T> Target type
 */
public final class ConstructorFromUnmarshaller<S, T> implements FromUnmarshaller<S, T> {

    private final Constructor<S> unmarshal;

    private final Class<S> boundClass;
    
    private final Class<T> targetClass;
    
    /**
     * Create a new instance
     * @param unmarshal Constructor to be used
     */
    public ConstructorFromUnmarshaller(Constructor<S> unmarshal) {
        
        this.boundClass = unmarshal.getDeclaringClass();
        
        if (getBoundClass().isInterface() 
                || Modifier.isAbstract(getBoundClass().getModifiers()) 
                || getBoundClass().isLocalClass() 
                || getBoundClass().isMemberClass()) {
            throw new IllegalStateException("unmarshal constructor must have an instantiable target class");
        }
        
        if (unmarshal.getParameterTypes().length != 1) {
        	throw new IllegalStateException("unmarshal constructor must have a single parameter");
        }
        
        this.unmarshal = unmarshal;
        
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
            return unmarshal.newInstance(object);
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
