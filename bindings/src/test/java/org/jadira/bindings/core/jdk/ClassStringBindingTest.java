/*
 *  Copyright 2010 Chris Pheby
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
package org.jadira.bindings.core.jdk;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.junit.Test;

public class ClassStringBindingTest {

    private static final ClassStringBinding BINDING = new ClassStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        String className = "java.net.URL";

        assertEquals(URL.class, BINDING.unmarshal(className));
        assertEquals(new URL[]{}.getClass(), BINDING.unmarshal(className + "[]"));
        assertEquals(new URL[][]{}.getClass(), BINDING.unmarshal(className + "[][]"));

        assertEquals(Long.TYPE, BINDING.unmarshal("long"));
        assertEquals(Boolean.TYPE, BINDING.unmarshal("boolean"));
        assertEquals(Float.TYPE, BINDING.unmarshal("float"));
        assertEquals(Short.TYPE, BINDING.unmarshal("short"));
        assertEquals(Byte.TYPE, BINDING.unmarshal("byte"));
        assertEquals(Double.TYPE, BINDING.unmarshal("double"));
        assertEquals(Character.TYPE, BINDING.unmarshal("char"));
        
        assertEquals(new long[]{}.getClass(), BINDING.unmarshal("long[]"));
        assertEquals(new boolean[]{}.getClass(), BINDING.unmarshal("boolean[]"));
        assertEquals(new float[]{}.getClass(), BINDING.unmarshal("float[]"));
        assertEquals(new short[]{}.getClass(), BINDING.unmarshal("short[]"));
        assertEquals(new byte[]{}.getClass(), BINDING.unmarshal("byte[]"));
        assertEquals(new double[]{}.getClass(), BINDING.unmarshal("double[]"));
        assertEquals(new char[]{}.getClass(), BINDING.unmarshal("char[]"));
        
        assertEquals(new long[][]{}.getClass(), BINDING.unmarshal("long[][]"));
    }
    
    @Test
    public void testMarshal() {

        assertEquals("org.jadira.bindings.core.jdk.ClassStringBindingTest", BINDING.marshal(ClassStringBindingTest.class));
        assertEquals("org.jadira.bindings.core.jdk.ClassStringBindingTest[]", BINDING.marshal(new ClassStringBindingTest[]{}.getClass()));
        assertEquals("org.jadira.bindings.core.jdk.ClassStringBindingTest[][]", BINDING.marshal(new ClassStringBindingTest[][]{}.getClass()));

        assertEquals("long", BINDING.marshal(Long.TYPE));
        assertEquals("boolean", BINDING.marshal(Boolean.TYPE));
        assertEquals("float", BINDING.marshal(Float.TYPE));
        assertEquals("short", BINDING.marshal(Short.TYPE));
        assertEquals("byte", BINDING.marshal(Byte.TYPE));
        assertEquals("double", BINDING.marshal(Double.TYPE));
        assertEquals("char", BINDING.marshal(Character.TYPE));
        
        assertEquals("long[]", BINDING.marshal(new long[]{}.getClass()));
        assertEquals("boolean[]", BINDING.marshal(new boolean[]{}.getClass()));
        assertEquals("float[]", BINDING.marshal(new float[]{}.getClass()));
        assertEquals("short[]", BINDING.marshal(new short[]{}.getClass()));
        assertEquals("byte[]", BINDING.marshal(new byte[]{}.getClass()));
        assertEquals("double[]", BINDING.marshal(new double[]{}.getClass()));
        assertEquals("char[]", BINDING.marshal(new char[]{}.getClass()));
        
        assertEquals("long[][]", BINDING.marshal(new long[][]{}.getClass()));
    }
}
