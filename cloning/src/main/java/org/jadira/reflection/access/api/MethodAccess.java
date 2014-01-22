package org.jadira.reflection.access.api;

import java.lang.reflect.Method;

/**
 * Defines a mechanism for accessing a specific method within a specific class
 * @param <C> The class containing the method to be accessed
 */
public interface MethodAccess<C> {	
	
	/**
	 * Get the Class containing the field being accessed
	 * @return The class
	 */
	public Class<C> declaringClass();
	
	/**
	 * The Class giving the type of the method result or null
	 * @return The result class
	 */
	public Class<?> returnClass();
	
	/**
	 * Get the Method being accessed
	 * @return The method
	 */
	public Method method();
	
	/**
	 * Invokes the method
	 * @param parent The instance to access the method for
	 * @param args The arguments to supply when invoking the method
	 * @return The result of the method invocation, or null for a void method
	 */
    Object invoke(C parent, Object ... args);
}
