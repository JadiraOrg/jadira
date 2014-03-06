package org.jadira.reflection.access.api;

/**
 * Defines a Factory for ClassAccess instances
 */
public interface ClassAccessFactory {

	/**
	 * Get a class access instance for the given class
	 * @param clazz The class
	 * @param <C> The type of class
	 * @return The ClassAccess instance
	 */
	<C> ClassAccess<C> getClassAccess(Class<C> clazz);
}
