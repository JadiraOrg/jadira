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
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.access.api.MethodAccess;
import org.jadira.reflection.core.misc.ClassUtils;

/**
 * ClassAccess implementation based on sun.misc.Unsafe
 * @param <C> The Class to be accessed
 */
public class UnsafeClassAccess<C> implements ClassAccess<C> {

    private static final ConcurrentHashMap<Class<?>, UnsafeClassAccess<?>> CLASS_ACCESSES = new ConcurrentHashMap<Class<?>, UnsafeClassAccess<?>>();
    
	private static final UnsafeOperations UNSAFE_OPERATIONS = UnsafeOperations.getUnsafeOperations();
	private Class<C> clazz;
	
	private String[] fieldNames;
	private UnsafeFieldAccess<C>[] fieldAccess;
	
	private UnsafeClassAccess(Class<C> clazz) {
		this.clazz = clazz;
		
		Field[] fields = ClassUtils.collectInstanceFields(clazz);
		
		String[] unsortedFieldNames = new String[fields.length];
		for (int i=0; i < unsortedFieldNames.length; i++) {
			unsortedFieldNames[i] = fields[i].getName();
		}
		fieldNames = Arrays.copyOf(unsortedFieldNames, unsortedFieldNames.length);
		Arrays.sort(fieldNames);
		
		@SuppressWarnings("unchecked")
		final UnsafeFieldAccess<C>[] myFieldAccess = (UnsafeFieldAccess<C>[])new UnsafeFieldAccess[fields.length];
		for (int i=0; i < fields.length; i++) {
			
			String fieldName = unsortedFieldNames[i];
			for (int tIdx = 0; tIdx < unsortedFieldNames.length; tIdx++) {
				if (fieldName.equals(fieldNames[tIdx])) {
					myFieldAccess[tIdx] = UnsafeFieldAccess.get(fields[i]);
					break;
				}
			}
			
		}
		fieldAccess = myFieldAccess;
	}
	
	@Override
	public C newInstance() {
		return UNSAFE_OPERATIONS.allocateInstance(clazz);
	}

	/**
	 * Get a new instance that can access the given Class
	 * @param clazz Class to be accessed
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
	public UnsafeFieldAccess<C>[] getFieldAccessors() {
		return fieldAccess;
	}
	
	@Override
	public UnsafeFieldAccess<C> getFieldAccess(Field f) {
		int idx = Arrays.binarySearch(fieldNames, f.getName());
		return fieldAccess[idx];
	}

	@Override
	public Class<C> getType() {
		return clazz;
	}

    @Override
    public MethodAccess<C>[] getMethodAccessors() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MethodAccess<C> getMethodAccess(Method f) {
        // TODO Auto-generated method stub
        return null;
    }
}
