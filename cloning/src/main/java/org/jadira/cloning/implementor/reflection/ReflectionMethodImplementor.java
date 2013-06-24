/*
 *  Copyright 2013 Chris Pheby
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
package org.jadira.cloning.implementor.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

import org.jadira.cloning.api.CloneDriver;
import org.jadira.cloning.api.CloneImplementor;

/**
 * A Clone Implementor that uses a method (e.g. factory method) on the target class to perform the
 * clone.
 */
public class ReflectionMethodImplementor implements CloneImplementor {

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	private final Class<?> boundClass;

	private final MethodHandle marshalHandle;

    /**
     * Create a new instance
     * @param marshal Method to be used
     */
	public ReflectionMethodImplementor(Method marshal) {

		if (marshal.getParameterTypes().length == 0 && Modifier.isStatic(marshal.getModifiers())) {
			throw new IllegalStateException("marshal method must either be instance scope or define a single parameter");
		} else if (marshal.getParameterTypes().length == 1 && (!Modifier.isStatic(marshal.getModifiers()))) {
			throw new IllegalStateException("marshal method must either be instance scope or define a single parameter");
		} else if (marshal.getParameterTypes().length >= 2) {
			throw new IllegalStateException("marshal method must either be instance scope or define a single parameter");
		}

		if (!marshal.getDeclaringClass().equals(marshal.getReturnType())) {
			throw new IllegalStateException("marshal method must return an instance of target class");
		}

		this.boundClass = marshal.getDeclaringClass();

		try {
			this.marshalHandle = LOOKUP.unreflect(marshal);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Method is not accessible" + marshal);
		}
	}

	@Override
	public <T> T clone(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse) {

		try {
			final T result = (T) marshalHandle.invoke(obj);
			return result;
		} catch (Throwable ex) {
			if (ex.getCause() instanceof RuntimeException) {
				throw (RuntimeException) ex.getCause();
			}
			throw new IllegalStateException(ex.getMessage(), ex.getCause());
		}
	}
	
	@Override
	public <T> T newInstance(Class<T> c) {
		throw new UnsupportedOperationException("newInstance() is unsupported");
	}

	@Override
	public boolean canClone(Class<?> clazz) {
		return boundClass.equals(clazz);
	}
}
