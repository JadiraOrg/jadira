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
package org.jadira.reflection.access.asm;

import java.lang.reflect.Method;

import org.jadira.reflection.access.api.MethodAccess;

public class AsmMethodAccess<C> implements MethodAccess<C> {

	private Method method;
	private AsmClassAccess<C> classAccess;
	private Class<C> declaringClass;
	private Class<?> returnType;
	
	@SuppressWarnings("unchecked")
	private AsmMethodAccess(AsmClassAccess<C> classAccess, Method m) {
					
		this.classAccess = classAccess;
		this.method = m;
		this.declaringClass = (Class<C>) m.getDeclaringClass();	
		
		this.returnType = (Class<?>) m.getReturnType();
	}
	
	public static final <C> AsmMethodAccess<C> get(AsmClassAccess<C> classAccess, Method m) {
		return new AsmMethodAccess<C>(classAccess, m);
	}
	
	@Override
	public Class<C> declaringClass() {
		return declaringClass;
	}

	@Override
	public Class<?> returnClass() {
		return returnType;
	}

	@Override
	public Method method() {
		return method;
	}
	
	@Override
	public Object invoke(Object target, Object... args) throws IllegalArgumentException {
		return classAccess.invokeMethod(target, method, args);
	}
}
