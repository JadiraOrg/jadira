/*
 *  Copyright 2013 Christopher Pheby
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
package org.jadira.reflection.access.unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.jadira.reflection.access.AbstractClassAccess;
import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.access.api.FieldAccess;
import org.jadira.reflection.access.api.MethodAccess;
import org.jadira.reflection.access.invokedynamic.InvokeDynamicMethodAccess;

/**
 * ClassAccess implementation based on sun.misc.Unsafe
 * @param <C> The Class to be accessed
 */
public class UnsafeClassAccess<C> extends AbstractClassAccess<C> implements ClassAccess<C> {

    private static final ConcurrentHashMap<Class<?>, UnsafeClassAccess<?>> CLASS_ACCESSES = new ConcurrentHashMap<Class<?>, UnsafeClassAccess<?>>();
    
	private static final UnsafeOperations UNSAFE_OPERATIONS = UnsafeOperations.getUnsafeOperations();

	private UnsafeClassAccess(Class<C> clazz) {
		super(clazz);
	}
	
	@Override
	public C newInstance() {
		return UNSAFE_OPERATIONS.allocateInstance(getType());
	}

	/**
	 * Get a new instance that can access the given Class
	 * @param clazz Class to be accessed
	 * @param <C> The type of class
	 * @return New UnsafeClassAccess instance
	 */
	public static <C> UnsafeClassAccess<C> get(Class<C> clazz) {
        @SuppressWarnings("unchecked")
        UnsafeClassAccess<C> access = (UnsafeClassAccess<C>) CLASS_ACCESSES.get(clazz);
        if (access != null) {
            return access;
        }
	    
        access = new UnsafeClassAccess<C>(clazz);
        
        CLASS_ACCESSES.putIfAbsent(clazz, access);
        return access;
	}
	
	@Override
	protected MethodAccess<C> constructMethodAccess(Method method) {
		return InvokeDynamicMethodAccess.get(method);
	}
	
	@Override
	protected FieldAccess<C> constructFieldAccess(Field field) {
		return UnsafeFieldAccess.get(field);
	}

	@Override
	protected <X> ClassAccess<X> constructClassAccess(Class<X> clazz) {
		return UnsafeClassAccess.get(clazz);
	}
}
