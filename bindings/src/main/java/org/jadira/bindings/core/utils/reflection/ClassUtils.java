/*
 *  Copyright 2010, 2011 Christopher Pheby
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
package org.jadira.bindings.core.utils.reflection;

import java.util.HashMap;
import java.util.Map;

import org.jadira.bindings.core.utils.string.StringUtils;

/**
 * Holds utility methods for obtaining a class from a symbolic representation
 * The result of {@link #determineQualifiedName(String)} is equivalent in format to the result of {@link Class#getName()}
 */
public class ClassUtils {

    public static final char PACKAGE_SEPARATOR_CHARACTER = '.';
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    private static final Map<String, String> primitiveMapping = new HashMap<String, String>();

    static {
        primitiveMapping.put("int", "I");
        primitiveMapping.put("boolean", "Z");
        primitiveMapping.put("float", "F");
        primitiveMapping.put("long", "J");
        primitiveMapping.put("short", "S");
        primitiveMapping.put("byte", "B");
        primitiveMapping.put("double", "D");
        primitiveMapping.put("char", "C");
    }

    private ClassUtils() {
    }

    /**
     * Attempt to load the class matching the given qualified name.
     * Uses the current Thread's class loader, or if unavailable, the classloader
     * this class was loaded from.
     * @param qualifiedName The qualified name to use
     * @throws IllegalArgumentException If the class cannot be loaded
     * @return The {@link Class} representing the given class
     */
    public static Class<?> getClass(String qualifiedName) {
        return getClass(ClassLoaderUtils.getClassLoader(), qualifiedName);
    }
    
    /**
     * Attempt to load the class matching the given qualified name
     * @param classLoader Classloader to use
     * @param className The classname to use
     * @throws IllegalArgumentException If the class cannot be loaded
     * @return The {@link Class} representing the given class
     */
    public static Class<?> getClass(ClassLoader classLoader, String className) {
        try {

            final Class<?> clazz;

            if (primitiveMapping.containsKey(className)) {

                String qualifiedName = "[" + primitiveMapping.get(className);
                clazz = Class.forName(qualifiedName, true, classLoader).getComponentType();
            } else {
                clazz = Class.forName(determineQualifiedName(className), true, classLoader);
            }
            return clazz;
        } catch (ClassNotFoundException ex) {

            int lastSeparatorIndex = className.lastIndexOf(PACKAGE_SEPARATOR_CHARACTER);

            if (lastSeparatorIndex != -1) {
                return getClass(classLoader, className.substring(0, lastSeparatorIndex) + INNER_CLASS_SEPARATOR_CHAR
                        + className.substring(lastSeparatorIndex + 1));
            }
            throw new IllegalArgumentException("Unable to unmarshall String to Class: " + className);
        }
    }

    /**
     * Given a readable class name determine the JVM Qualified Name
     * @param readableClassName The name to convert
     * @return The JVM Qualified representation
     */
    public static String determineQualifiedName(String readableClassName) {

        readableClassName = StringUtils.removeWhitespace(readableClassName);
        if (readableClassName == null) {

            throw new IllegalArgumentException("readableClassName must not be null.");

        } else if (readableClassName.endsWith("[]")) {

            StringBuilder classNameBuffer = new StringBuilder();

            while (readableClassName.endsWith("[]")) {
                readableClassName = readableClassName.substring(0, readableClassName.length() - 2);
                classNameBuffer.append("[");
            }

            String abbreviation = primitiveMapping.get(readableClassName);

            if (abbreviation == null) {
                classNameBuffer.append("L").append(readableClassName).append(";");
            } else {
                classNameBuffer.append(abbreviation);
            }

            readableClassName = classNameBuffer.toString();
        }
        return readableClassName;
    }
    
    /**
     * Given a JVM Qualified Name produce a readable classname
     * @param qualifiedName The Qualified Name
     * @return The readable classname
     */
    public static String determineReadableClassName(String qualifiedName) {

        qualifiedName = StringUtils.removeWhitespace(qualifiedName);
        if (qualifiedName == null) {

            throw new IllegalArgumentException("qualifiedName must not be null.");
        } else if (qualifiedName.startsWith("[")) {

            StringBuilder classNameBuffer = new StringBuilder();
            while (qualifiedName.startsWith("[")) {
                classNameBuffer.append("[]");
                qualifiedName = qualifiedName.substring(1);
            }
            
            if (primitiveMapping.containsValue(qualifiedName)) {
                
                for (Map.Entry<String,String> next : primitiveMapping.entrySet()) {
                    if (next.getValue().equals(qualifiedName)) {
                        qualifiedName = next.getKey() + classNameBuffer.toString();
                        break;
                    }
                }
                
            } else if (qualifiedName.startsWith("L") && qualifiedName.endsWith(";")) {
                qualifiedName = qualifiedName.substring(1, qualifiedName.length() - 1) + classNameBuffer.toString();
            } else {
                throw new IllegalArgumentException("qualifiedName was invalid {" + qualifiedName + "}");                
            }
        } else if (qualifiedName.endsWith("]")) {
            throw new IllegalArgumentException("qualifiedName was invalid {" + qualifiedName + "}");
        }
        
        return qualifiedName;
    }
}
