/*
 *  Copyright 2012 Christopher Pheby
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
package org.jadira.usertype.spi.utils.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.util.Arrays;

public final class ReflectionUtils {

	private ReflectionUtils() {
	}
	
	private static final ReflectPermission SUPPRESS_ACCESS_CHECKS = new ReflectPermission("suppressAccessChecks");
	
	public static boolean isPrivateAccessAllowed() {

		final SecurityManager manager = System.getSecurityManager();
		
		if (manager != null) {
			
			try {
				manager.checkPermission(SUPPRESS_ACCESS_CHECKS);
			} catch (final SecurityException ex) {
				return false;
			}
		}
		return true;
	}
	
	public static Field findField(Class<?> clazz, String fieldName) {

		Class<?> typeBeingSearched = clazz;
		while (typeBeingSearched != null && !typeBeingSearched.equals(Object.class)) {
			
			Field[] fields = typeBeingSearched.getDeclaredFields();
			
			for (Field field : fields) {
				if (fieldName.equals(field.getName())) {
					return field;
				}
			}
			
			typeBeingSearched = typeBeingSearched.getSuperclass();
		}
		throw new IllegalStateException("Field (" + fieldName + ") could not be found in " + clazz.getName());
	}
    
	public static <A> Constructor<A> findConstructor(Class<A> clazz, Class<?>... paramTypes) {

		Constructor<A> constructor;
		try {
			constructor = clazz.getDeclaredConstructor(paramTypes);
		} catch (SecurityException e) {
			throw new IllegalStateException("Constructor for (" + clazz.getName() + ") could not be accessed");
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Constructor for (" + clazz.getName() + ") could not be found");
		}
		return constructor;
	}
	
	public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {

		Class<?> typeBeingSearched = clazz;
		while (typeBeingSearched != null) {
			
			Method[] methods = (typeBeingSearched.isInterface() ? typeBeingSearched.getMethods() : typeBeingSearched.getDeclaredMethods());
			
			for (Method method : methods) {
				if (methodName.equals(method.getName()) && (Arrays.equals(paramTypes, method.getParameterTypes()))) {
					return method;
				}
			}
			
			typeBeingSearched = typeBeingSearched.getSuperclass();
		}
		throw new IllegalStateException("Method (" + methodName + ") could not be found in " + clazz.getName());
	}

	public static Object getFieldValue(Field field, Object target) {
			try {
				return field.get(target);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException("Unexpected exception accessing field {" + field.getName() + "} of {" + field.getDeclaringClass() + "} : " + e.getMessage());
			}
	}
}
