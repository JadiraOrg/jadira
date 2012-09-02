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
package org.jadira.bindings.core.general;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.jadira.bindings.core.general.binding.MethodConstructorBinding;
import org.jadira.bindings.core.test.SubjectA;
import org.junit.Before;
import org.junit.Test;

public class MethodConstructorBindingTest {

    private MethodConstructorBinding<SubjectA, String> binding ;
    
    @Before
    public void setup() throws SecurityException, NoSuchMethodException {
        
        Method method = SubjectA.class.getMethod("marshalMethodA", new Class[]{});
        Constructor<SubjectA> constructor = SubjectA.class.getConstructor(new Class[]{ String.class });
        binding = new MethodConstructorBinding<SubjectA, String>(constructor, String.class, method);
    }
    
    @Test
    public void testUnmarshalUsingConstructor() throws SecurityException, NoSuchMethodException {
       
        assertEquals(new SubjectA("UNMARSHALLED_BY_CONSTRUCTOR"), binding.unmarshal("UNMARSHALLED_BY_CONSTRUCTOR"));
    }   
    
    @Test
    public void testMarshalUsingMethod() throws SecurityException, NoSuchMethodException {
        
        assertEquals("1:MARSHALLED_BY_A", binding.marshal(new SubjectA("1")));
    }
}
