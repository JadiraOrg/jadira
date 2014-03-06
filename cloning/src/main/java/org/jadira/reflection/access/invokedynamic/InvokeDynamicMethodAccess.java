package org.jadira.reflection.access.invokedynamic;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import org.dynalang.dynalink.DefaultBootstrapper;
import org.jadira.reflection.access.api.MethodAccess;

public class InvokeDynamicMethodAccess<C> implements MethodAccess<C> {

	private String methodName;
	
	private Method method;
	private Class<C> declaringClass;
	private Class<?> returnType;

	CallSite methodCallSite;
	
	MethodHandle mh;
	
	@SuppressWarnings("unchecked")
	private InvokeDynamicMethodAccess(Method m) {
		
		this.methodName = m.getName();
		
		this.declaringClass = (Class<C>) m.getDeclaringClass();	
		this.method = m;

		this.returnType = (Class<?>) m.getReturnType();
		
		methodCallSite = DefaultBootstrapper.publicBootstrap(null, "dyn:getMethod:" + methodName, MethodType.methodType(returnType, declaringClass, method.getParameterTypes()));	
		mh = methodCallSite.dynamicInvoker();		
	}
	
	public static <C> InvokeDynamicMethodAccess<C> get(Method m) {
		
		return new InvokeDynamicMethodAccess<C>(m);
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
            return mh.invokeExact(target, args);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Problem invoking {" + method.getName() + "} of object {"
                    + System.identityHashCode(target) + "}: " + e.getMessage(), e);
        }
	}
}
