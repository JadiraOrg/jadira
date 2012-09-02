/*
 *  Copyright 2010 Christopher Pheby
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

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.junit.Test;

public class ClassUtilsTest {

    @Test
    public void testGetClass() {
        
        String className = "java.net.URL";

        assertEquals(URL.class, ClassUtils.getClass(getClass().getClassLoader(), className));
        assertEquals(URL.class, ClassUtils.getClass(getClass().getClassLoader(), className + " "));
        assertEquals(new URL[]{}.getClass(), ClassUtils.getClass(getClass().getClassLoader(), className + "[]"));
        assertEquals(new URL[][]{}.getClass(), ClassUtils.getClass(getClass().getClassLoader(), className + "[][]"));

        assertEquals(Long.TYPE, ClassUtils.getClass(getClass().getClassLoader(), "long"));
        assertEquals(Boolean.TYPE, ClassUtils.getClass(getClass().getClassLoader(), "boolean"));
        assertEquals(Float.TYPE, ClassUtils.getClass(getClass().getClassLoader(), "float"));
        assertEquals(Short.TYPE, ClassUtils.getClass(getClass().getClassLoader(), "short"));
        assertEquals(Byte.TYPE, ClassUtils.getClass(getClass().getClassLoader(), "byte"));
        assertEquals(Double.TYPE, ClassUtils.getClass(getClass().getClassLoader(), "double"));
        assertEquals(Character.TYPE, ClassUtils.getClass(getClass().getClassLoader(), "char"));
        
        assertEquals(new long[]{}.getClass(), ClassUtils.getClass(getClass().getClassLoader(), "long[]"));
        assertEquals(new boolean[]{}.getClass(), ClassUtils.getClass(getClass().getClassLoader(), "boolean[]"));
        assertEquals(new float[]{}.getClass(), ClassUtils.getClass(getClass().getClassLoader(), "float[]"));
        assertEquals(new short[]{}.getClass(), ClassUtils.getClass(getClass().getClassLoader(), "short[]"));
        assertEquals(new byte[]{}.getClass(), ClassUtils.getClass(getClass().getClassLoader(), "byte[]"));
        assertEquals(new double[]{}.getClass(), ClassUtils.getClass(getClass().getClassLoader(), "double[]"));
        assertEquals(new char[]{}.getClass(), ClassUtils.getClass(getClass().getClassLoader(), "char[]"));
        
        assertEquals(new long[][]{}.getClass(), ClassUtils.getClass(getClass().getClassLoader(), "long[][]"));        
    }
    
    @Test
    public void testDetermineQualifiedName() {
        
        String className = "org.jadira.bindings.core.utils.string.ClassUtilsTest";
        
        assertEquals("org.jadira.bindings.core.utils.string.ClassUtilsTest", ClassUtils.determineQualifiedName(className));
        assertEquals("org.jadira.bindings.core.utils.string.ClassUtilsTest", ClassUtils.determineQualifiedName(className + " "));
        assertEquals("[Lorg.jadira.bindings.core.utils.string.ClassUtilsTest;", ClassUtils.determineQualifiedName(className + "[]"));
        assertEquals("[[Lorg.jadira.bindings.core.utils.string.ClassUtilsTest;", ClassUtils.determineQualifiedName(className + "[][]"));

        assertEquals("long", ClassUtils.determineQualifiedName("long"));
        assertEquals("boolean", ClassUtils.determineQualifiedName("boolean"));
        assertEquals("float", ClassUtils.determineQualifiedName("float"));
        assertEquals("short", ClassUtils.determineQualifiedName("short"));
        assertEquals("byte", ClassUtils.determineQualifiedName("byte"));
        assertEquals("double", ClassUtils.determineQualifiedName("double"));
        assertEquals("char", ClassUtils.determineQualifiedName("char"));
        
        assertEquals("[J", ClassUtils.determineQualifiedName("long[]"));
        assertEquals("[Z", ClassUtils.determineQualifiedName("boolean[]"));
        assertEquals("[F", ClassUtils.determineQualifiedName("float[]"));
        assertEquals("[S", ClassUtils.determineQualifiedName("short[]"));
        assertEquals("[B", ClassUtils.determineQualifiedName("byte[]"));
        assertEquals("[D", ClassUtils.determineQualifiedName("double[]"));
        assertEquals("[C", ClassUtils.determineQualifiedName("char[]"));
        
        assertEquals("[[J", ClassUtils.determineQualifiedName("long[][]"));
        
        assertEquals("[[Ljava.net.URL;", ClassUtils.determineQualifiedName("java.net.URL[][]"));
    }
    
    @Test
    public void testDetermineReadableClassName() {
        
        String className = "org.jadira.bindings.core.utils.string.ClassUtilsTest";
        
        assertEquals("org.jadira.bindings.core.utils.string.ClassUtilsTest", ClassUtils.determineReadableClassName(className));
        assertEquals("org.jadira.bindings.core.utils.string.ClassUtilsTest", ClassUtils.determineReadableClassName(className + " "));
        assertEquals("org.jadira.bindings.core.utils.string.ClassUtilsTest[]", ClassUtils.determineReadableClassName("[L" + className + ";"));
        assertEquals("org.jadira.bindings.core.utils.string.ClassUtilsTest[][]", ClassUtils.determineReadableClassName("[[L" + className + ";"));

        assertEquals("long", ClassUtils.determineReadableClassName("long"));
        assertEquals("boolean", ClassUtils.determineReadableClassName("boolean"));
        assertEquals("float", ClassUtils.determineReadableClassName("float"));
        assertEquals("short", ClassUtils.determineReadableClassName("short"));
        assertEquals("byte", ClassUtils.determineReadableClassName("byte"));
        assertEquals("double", ClassUtils.determineReadableClassName("double"));
        assertEquals("char", ClassUtils.determineReadableClassName("char"));
        
        assertEquals("long[]", ClassUtils.determineReadableClassName("[J"));
        assertEquals("boolean[]", ClassUtils.determineReadableClassName("[Z"));
        assertEquals("float[]", ClassUtils.determineReadableClassName("[F"));
        assertEquals("short[]", ClassUtils.determineReadableClassName("[S"));
        assertEquals("byte[]", ClassUtils.determineReadableClassName("[B"));
        assertEquals("double[]", ClassUtils.determineReadableClassName("[D"));
        assertEquals("char[]", ClassUtils.determineReadableClassName("[C"));
        
        assertEquals("long[][]", ClassUtils.determineReadableClassName("[[J"));
        
        assertEquals("java.net.URL[][]", ClassUtils.determineReadableClassName("[[Ljava.net.URL;"));
    }
}
