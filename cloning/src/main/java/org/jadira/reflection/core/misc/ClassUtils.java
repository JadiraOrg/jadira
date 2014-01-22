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
package org.jadira.reflection.core.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility methods for inspecting and interpreting classes
 */
public final class ClassUtils {
    
    private ClassUtils() {
    }

    /**
     * Indicate if the class is a known non-primitive, JDK immutable type
     * @param type Class to test
     * @return True if the type is an immutable from the JDK class libraries
     */
    public static boolean isJdkImmutable(Class<?> type) {
        
    	if (Class.class == type) {
    		return true;
    	}
        if (String.class == type) {
            return true;
        }
        if (BigInteger.class == type) {
            return true;
        }
        if (BigDecimal.class == type) {
            return true;
        }
        if (URL.class == type) {
            return true;
        }
        if (UUID.class == type) {
            return true;
        }
        if (URI.class == type) {
            return true;
        }
        if (Pattern.class == type) {
            return true;
        }
        return false;
    }

    /**
     * Indicate if the class is a known primitive wrapper type
     * @param type Class to test
     * @return True if the type is a primitive wrapper type
     */
    public static boolean isWrapper(Class<?> type) {

        if (Boolean.class == type) {
            return true;
        } 
        if (Byte.class == type) {
            return true;
        } 
        if (Character.class == type) {
            return true;
        } 
        if (Short.class == type) {
            return true;
        } 
        if (Integer.class == type) {
            return true;
        } 
        if (Long.class == type) {
            return true;
        }
        if (Float.class == type) {
            return true;
        } 
        if (Double.class == type) {
            return true;
        }
        return false;
    }

//    public static Field[] collectFields(Class<?> c) {
//        
//        return collectFields(c, 0, 0);
//    }
    
    /**
     * Produces an array with all the instance fields of the specified class
     * @param c The class specified
     * @return The array of matched Fields
     */
    public static Field[] collectInstanceFields(Class<?> c) {
    	
        return collectFields(c, 0, Modifier.STATIC);
    }

    /**
	 * Produces an array with all the instance fields of the specified class which match the supplied rules
     * @param c The class specified
     * @param excludePublic Exclude public fields if true
     * @param excludeProtected Exclude protected fields if true
     * @param excludePrivate Exclude private fields if true
     * @return The array of matched Fields
     */
    public static Field[] collectInstanceFields(Class<?> c, boolean excludePublic, boolean excludeProtected, boolean excludePrivate) {
        
    	int inclusiveModifiers = 0;
    	int exclusiveModifiers = Modifier.STATIC;
    	
    	if (excludePrivate) {
    		exclusiveModifiers += Modifier.PRIVATE;
    	}
    	if (excludePublic) {
    		exclusiveModifiers += Modifier.PUBLIC;
    	}
    	if (excludeProtected) {
    		exclusiveModifiers += Modifier.PROTECTED;
    	}
    	
        return collectFields(c, inclusiveModifiers, exclusiveModifiers);
    }
    
    /**
	 * Produces an array with all the instance fields of the specified class which match the supplied rules
     * @param c The class specified
     * @param inclusiveModifiers An int indicating the {@link Modifier}s that may be applied
     * @param excludeModifiers An int indicating the {@link Modifier}s that must be excluded
     * @return The array of matched Fields
     */
    public static Field[] collectFields(Class<?> c, int inclusiveModifiers, int exclusiveModifiers) {
        
        Set<Field> fields = new HashSet<Field>();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if (((f.getModifiers() & exclusiveModifiers) == 0)
                		&& ((f.getModifiers() & inclusiveModifiers) == inclusiveModifiers)) {
                    fields.add(f);
                    f.setAccessible(true);
                }
            }
            c = c.getSuperclass();
        }
        return fields.toArray(new Field[]{});
    }
    
    /**
     * Produces an array with all the methods of the specified class
     * @param c The class specified
     * @return The array of matched Methods
     */
    public static Method[] collectMethods(Class<?> c) {
    	
        return collectMethods(c, 0, 0);
    }

    /**
     * Produces an array with all the methods of the specified class
     * @param c The class specified
     * @param inclusiveModifiers An int indicating the {@link Modifier}s that may be applied
     * @param excludeModifiers An int indicating the {@link Modifier}s that must be excluded
     * @return The array of matched Methods
     */
    public static Method[] collectMethods(Class<?> c, int inclusiveModifiers, int exclusiveModifiers) {
        
        Set<Method> methods = new HashSet<Method>();
        while (c != Object.class) {
            for (Method f : c.getDeclaredMethods()) {
                if (((f.getModifiers() & exclusiveModifiers) == 0)
                		&& ((f.getModifiers() & inclusiveModifiers) == inclusiveModifiers)) {
                    methods.add(f);
                    f.setAccessible(true);
                }
            }
            c = c.getSuperclass();
        }
        return methods.toArray(new Method[]{});
    }
}
