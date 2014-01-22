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
package org.jadira.reflection.access.classloader;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A ClassLoader which can be used to load classes from arbitrary byte arrays.
 * Jadira uses this to load classes generated using ASM.
 */
public class AccessClassLoader extends ClassLoader {

	private static final Method DEFINE_METHOD;
	
	static {
		Method defineMethod = null;
		try {
			defineMethod = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class });
			defineMethod.setAccessible(true);
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}
		DEFINE_METHOD = defineMethod;
	}
	 
	private static final ConcurrentHashMap<ClassLoader, AccessClassLoader> ASM_CLASS_LOADERS = new ConcurrentHashMap<ClassLoader, AccessClassLoader>();

	private static final Map<String, byte[]> registeredClasses = new HashMap<String, byte[]>();
	
	/**
	 * Creates a new instance using a suitable ClassLoader for the specified class
	 * @param typeToBeExtended The class to use to obtain a ClassLoader
	 * @return A new instance, or an existing instance if one already exists.
	 */
	public static final AccessClassLoader get(Class<?> typeToBeExtended) {

		ClassLoader loader = typeToBeExtended.getClassLoader();
		return get(loader == null ? ClassLoader.getSystemClassLoader() : loader);
	}
	
	/**
	 * Creates an AccessClassLoader for the given parent
	 * @param parent The parent ClassLoader for this instance
	 * @return A new instance, or an existing instance if one already exists.
	 */
	public synchronized static final AccessClassLoader get(ClassLoader parent) {
		AccessClassLoader loader = (AccessClassLoader) ASM_CLASS_LOADERS.get(parent);
		if (loader == null) {
			loader = new AccessClassLoader(parent);
			ASM_CLASS_LOADERS.put(parent, loader);
		}
		return loader;
	}

	private AccessClassLoader(ClassLoader parentClassLoader) {
		super(parentClassLoader);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {

		Class<?> loadedClass = findLoadedClass(name);

		if (loadedClass == null) {
			
			try {
				loadedClass = findClass(name);
			} catch (ClassNotFoundException e) {
				// Ignore
			}
			
			if (loadedClass == null) {
				loadedClass = super.loadClass(name);
			}
		}

		return loadedClass;
	}
	
	/**
	 * Registers a class by its name
	 * @param name The name of the class to be registered
	 * @param bytes An array of bytes containing the class
	 */
	public void registerClass(String name, byte[] bytes) {
	    
		if (registeredClasses.containsKey(name)) {
			throw new IllegalStateException("Attempted to register a class that has been registered already: " + name);
		}
		registeredClasses.put(name, bytes);
	}
	
	@Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
	    
		byte[] bytes = registeredClasses.get(name);
		if (bytes != null) {
			registeredClasses.remove(name);
			try {
				return (Class<?>) DEFINE_METHOD.invoke(getParent(), new Object[] { name, bytes, Integer.valueOf(0), Integer.valueOf(bytes.length) });
			} catch (Exception ignored) {
			}
			return defineClass(name, bytes, 0, bytes.length);
		}
		throw new ClassNotFoundException("Cannot find class: " + name);
    }
}
