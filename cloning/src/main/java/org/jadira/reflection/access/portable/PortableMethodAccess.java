package org.jadira.reflection.access.portable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jadira.reflection.access.api.MethodAccess;

public class PortableMethodAccess<C> implements MethodAccess<C> {

	private Method method;
	private Class<C> declaringClass;
	private Class<?> returnType;

	@SuppressWarnings("unchecked")
	private PortableMethodAccess(Method m) {
		this.method = m;
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}
		this.declaringClass = (Class<C>) m.getDeclaringClass();
		this.returnType = (Class<?>) m.getReturnType();
	}
	
	public static <C> PortableMethodAccess<C> get(Method m) {
		
		return new PortableMethodAccess<C>(m);
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
		
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Could not invoke: " + e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Could not invoke: " + e.getMessage(), e);
		}
	}
}
