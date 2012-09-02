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
package org.jadira.bindings.core;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jadira.bindings.core.annotation.DefaultBinding;
import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.api.FromUnmarshaller;
import org.jadira.bindings.core.api.ToMarshaller;
import org.jadira.bindings.core.binder.BasicBinder;
import org.jadira.bindings.core.binder.Binder;
import org.jadira.bindings.core.general.binding.CompositeBinding;
import org.jadira.bindings.core.jdk.AtomicBooleanStringBinding;
import org.jadira.bindings.core.test.Narrow;
import org.jadira.bindings.core.test.SubjectC;
import org.jadira.bindings.core.test.SubjectD;
import org.junit.Test;

public class BasicBinderTest {

    private static final Binder BINDER = new BasicBinder();

    /**
     * Test method for {@link org.jadira.bindings.core.binder.BasicBinder#convertTo(java.lang.Object)}.
     */
    @Test
    public void testConvertToString() {

//        BigInteger integer = new BigInteger("10");
//        assertEquals("10", BINDER.convertTo(BigInteger.class, String.class, integer));
        
        SubjectC sub = new SubjectC("Test");
        assertEquals("Test:MARSHALLED_BY_A", BINDER.convertTo(SubjectC.class, String.class, sub));
    }

    /**
     * Test method for {@link org.jadira.bindings.core.BasicStringBinder#convertToString(java.lang.Object, java.lang.Class)}.
     */
    @Test
    public void testConvertToStringWithQualifier() {
        SubjectD sub = new SubjectD("Test");
        assertEquals("Test:MARSHALLED_BY_A", BINDER.convertTo(SubjectD.class, String.class, sub, Narrow.class));
    }

    /**
     * Test method for {@link org.jadira.bindings.core.BasicStringBinder#convertFromString(java.lang.Class, java.lang.String)}.
     */
    @Test
    public void testConvertFromString() {
        assertEquals(new SubjectD("Test"), BINDER.convertTo(String.class, SubjectD.class, "Test:MARSHALLED_BY_B"));
    }

    /**
     * Test method for {@link org.jadira.bindings.core.BasicStringBinder#convertFromString(java.lang.Class, java.lang.String, java.lang.Class)}.
     */
    @Test
    public void testConvertFromStringWithQualifier() {
        assertEquals(new SubjectD("Test"), BINDER.convertTo(String.class, SubjectD.class, "Test", Narrow.class));
    }

    /**
     * Test method for {@link org.jadira.bindings.core.binder.BasicBinder#findBinding(java.lang.Class, java.lang.Class)}.
     */
    @Test
    public void testFindBinding() {
        Binding<SubjectD, String> match = BINDER.findBinding(SubjectD.class, String.class);
        assertEquals(CompositeBinding.class, match.getClass());
        
        Binding<AtomicBoolean, String> match2 = BINDER.findBinding(AtomicBoolean.class, String.class);
        assertEquals(AtomicBooleanStringBinding.class, match2.getClass());
    }

    /**
     * Test method for {@link org.jadira.bindings.core.BasicStringBinder#findBinding(java.lang.Class, java.lang.Class)}.
     */
    @Test
    public void testFindBindingWithQualifier() {
        Binding<SubjectD, String> match = BINDER.findBinding(SubjectD.class, String.class, Narrow.class);
        assertEquals(CompositeBinding.class, match.getClass());
        
        Binding<AtomicBoolean, String> match2 = BINDER.findBinding(AtomicBoolean.class, String.class, DefaultBinding.class);
        assertEquals(AtomicBooleanStringBinding.class, match2.getClass());
    }

    /**
     * Test method for {@link org.jadira.bindings.core.BasicStringBinder#findMarshaller(java.lang.Class)}.
     */
    @Test
    public void testFindMarshaller() {
        ToMarshaller<SubjectD, String> match = BINDER.findMarshaller(SubjectD.class, String.class);
        assertEquals(CompositeBinding.class, match.getClass());
        
        ToMarshaller<AtomicBoolean, String> match2 = BINDER.findMarshaller(AtomicBoolean.class, String.class);
        assertEquals(AtomicBooleanStringBinding.class, match2.getClass());
    }

    /**
     * Test method for {@link org.jadira.bindings.core.BasicStringBinder#findMarshaller(java.lang.Class, java.lang.Class)}.
     */
    @Test
    public void testFindMarshallerWithQualifier() {
        ToMarshaller<SubjectD, String> match = BINDER.findMarshaller(SubjectD.class, String.class, Narrow.class);
        assertEquals(CompositeBinding.class, match.getClass());
        
        ToMarshaller<AtomicBoolean, String> match2 = BINDER.findMarshaller(AtomicBoolean.class, String.class, DefaultBinding.class);
        assertEquals(AtomicBooleanStringBinding.class, match2.getClass());
    }

    /**
     * Test method for {@link org.jadira.bindings.core.BasicStringBinder#findUnmarshaller(java.lang.Class)}.
     */
    @Test
    public void testFindUnmarshaller() {
        FromUnmarshaller<SubjectD, String> match = BINDER.findUnmarshaller(SubjectD.class, String.class);
        assertEquals(CompositeBinding.class, match.getClass());
        
        FromUnmarshaller<AtomicBoolean, String> match2 = BINDER.findUnmarshaller(AtomicBoolean.class, String.class);
        assertEquals(AtomicBooleanStringBinding.class, match2.getClass());
    }

    /**
     * Test method for {@link org.jadira.bindings.core.BasicStringBinder#findUnmarshaller(java.lang.Class, java.lang.Class)}.
     */
    @Test
    public void testFindUnmarshallerWithQualifier() {
        FromUnmarshaller<SubjectD, String> match = BINDER.findUnmarshaller(SubjectD.class, String.class, Narrow.class);
        assertEquals(CompositeBinding.class, match.getClass());
        
        FromUnmarshaller<AtomicBoolean, String> match2 = BINDER.findUnmarshaller(AtomicBoolean.class, String.class, DefaultBinding.class);
        assertEquals(AtomicBooleanStringBinding.class, match2.getClass());
    }
}
