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
package org.jadira.reflection.access.portable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.jadira.reflection.access.AbstractClassAccess;
import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.access.api.FieldAccess;
import org.jadira.reflection.access.api.MethodAccess;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * ClassAccess implementation which should be portable across most JVMs. 
 * Uses Reflection to access methods, and Objenesis to invoke constructors.
 * @param <C> The Class to be accessed
 */
public class PortableClassAccess<C> extends AbstractClassAccess<C> implements ClassAccess<C> {

    private static final ConcurrentHashMap<Class<?>, PortableClassAccess<?>> CLASS_ACCESSES = new ConcurrentHashMap<Class<?>, PortableClassAccess<?>>();
    
    private static final Objenesis OBJENESIS = new ObjenesisStd();
		
	private PortableClassAccess(Class<C> clazz) {
		super(clazz);
	}
	
	@Override
	public C newInstance() {
		return (C) OBJENESIS.newInstance(getType());
	}

	/**
	 * Get a new instance that can access the given Class. If the ClassAccess for this class
	 * has not been obtained before, then the specific PortableClassAccess is created by generating
	 * a specialised subclass of this class and returning it. 
	 * @param clazz Class to be accessed
	 * @param <C> The type of class
	 * @return New PortableClassAccess instance
	 */
	public static <C> PortableClassAccess<C> get(Class<C> clazz) {

        @SuppressWarnings("unchecked")
        PortableClassAccess<C> access = (PortableClassAccess<C>) CLASS_ACCESSES.get(clazz);
        if (access != null) {
            return access;
        }
        access = new PortableClassAccess<C>(clazz);
        CLASS_ACCESSES.putIfAbsent(clazz, access);
        return access;
	}

	@Override
	protected MethodAccess<C> constructMethodAccess(Method method) {
		return PortableMethodAccess.get(method);
	}
	
	@Override
	protected FieldAccess<C> constructFieldAccess(Field field) {
		return PortableFieldAccess.get(field);
	}
	
	@Override
	protected <X> ClassAccess<X> constructClassAccess(Class<X> clazz) {
		return PortableClassAccess.get(clazz);
	}
}
