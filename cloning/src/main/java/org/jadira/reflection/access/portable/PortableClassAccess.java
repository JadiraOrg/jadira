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
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.core.misc.ClassUtils;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * ClassAccess implementation which should be portable across most JVMs. 
 * Uses Reflection to access methods, and Objenesis to invoke constructors.
 * @param <C> The Class to be accessed
 */
public class PortableClassAccess<C> implements ClassAccess<C> {

    private static final ConcurrentHashMap<Class<?>, PortableClassAccess<?>> CLASS_ACCESSES = new ConcurrentHashMap<Class<?>, PortableClassAccess<?>>();
    
    private static final Objenesis OBJENESIS = new ObjenesisStd();
	
	private Class<C> clazz;

	private String[] fieldNames;
	private PortableFieldAccess<C>[] fieldAccess;
	
	private String[] methodNames;
	private PortableMethodAccess<C>[] methodAccess;
	
	private PortableClassAccess(Class<C> clazz) {
		
		this.clazz = clazz;

		Field[] fields = ClassUtils.collectInstanceFields(clazz);
		
		String[] unsortedFieldNames = new String[fields.length];
		for (int i=0; i < unsortedFieldNames.length; i++) {
			unsortedFieldNames[i] = fields[i].getName();
		}
		fieldNames = Arrays.copyOf(unsortedFieldNames, unsortedFieldNames.length);
		Arrays.sort(fieldNames);
		
		@SuppressWarnings("unchecked")
		final PortableFieldAccess<C>[] myFieldAccess = (PortableFieldAccess<C>[])new PortableFieldAccess[fields.length];
		for (int i=0; i < fields.length; i++) {
			
			String fieldName = unsortedFieldNames[i];
			for (int tIdx = 0; tIdx < unsortedFieldNames.length; tIdx++) {
				if (fieldName.equals(fieldNames[tIdx])) {
					myFieldAccess[tIdx] = PortableFieldAccess.get(fields[i]);
					break;
				}
			}
			
		}
		fieldAccess = myFieldAccess;
		
		Method[] methods = ClassUtils.collectMethods(clazz);
		
		String[] unsortedMethodNames = new String[methods.length];
		for (int i=0; i < unsortedMethodNames.length; i++) {
			unsortedMethodNames[i] = methods[i].getName();
		}
		methodNames = Arrays.copyOf(unsortedMethodNames, unsortedMethodNames.length);
		Arrays.sort(methodNames);
		
		@SuppressWarnings("unchecked")
		final PortableMethodAccess<C>[] myMethodAccess = (PortableMethodAccess<C>[])new PortableMethodAccess[methods.length];
		for (int i=0; i < methods.length; i++) {
			
			String methodName = unsortedMethodNames[i];
			for (int tIdx = 0; tIdx < unsortedMethodNames.length; tIdx++) {
				if (methodName.equals(methodNames[tIdx])) {
					myMethodAccess[tIdx] = PortableMethodAccess.get(methods[i]);
					break;
				}
			}
			
		}
		methodAccess = myMethodAccess;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public C newInstance() {
		return (C) OBJENESIS.newInstance(clazz);
	}

	/**
	 * Get a new instance that can access the given Class. If the ClassAccess for this class
	 * has not been obtained before, then the specific PortableClassAccess is created by generating
	 * a specialised subclass of this class and returning it. 
	 * @param clazz Class to be accessed
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
	public Class<C> getType() {
		return clazz;
	}

	@Override
	public PortableFieldAccess<C>[] getFieldAccessors() {
		return fieldAccess;
	}
	
	@Override
	public PortableFieldAccess<C> getFieldAccess(Field f) {
		int idx = Arrays.binarySearch(fieldNames, f.getName());
		return fieldAccess[idx];
	}

	@Override
	public PortableMethodAccess<C>[] getMethodAccessors() {
		return methodAccess;
	}
	
	@Override
	public PortableMethodAccess<C> getMethodAccess(Method m) {
		int idx = Arrays.binarySearch(methodNames, m.getName());
		return methodAccess[idx];
	}
}
