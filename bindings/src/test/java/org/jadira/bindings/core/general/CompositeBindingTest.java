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

import java.lang.reflect.Method;

import org.jadira.bindings.core.general.binding.CompositeBinding;
import org.jadira.bindings.core.general.marshaller.MethodToMarshaller;
import org.jadira.bindings.core.general.unmarshaller.MethodFromUnmarshaller;
import org.jadira.bindings.core.test.SubjectA;
import org.junit.Before;
import org.junit.Test;

public class CompositeBindingTest {

    private CompositeBinding<SubjectA, String> binding ;
    
    @Before
    public void setup() throws SecurityException, NoSuchMethodException {
        
        Method method = SubjectA.class.getMethod("unmarshalMethodA", new Class[]{ String.class });
        MethodFromUnmarshaller<SubjectA, String> unmarshaller = new MethodFromUnmarshaller<SubjectA, String>(SubjectA.class, method);

        Method method2 = SubjectA.class.getMethod("marshalMethodA", new Class[]{});
        MethodToMarshaller<SubjectA, String> marshaller = new MethodToMarshaller<SubjectA, String>(SubjectA.class, String.class, method2);
        
        binding = new CompositeBinding<SubjectA, String>(marshaller, unmarshaller);
    }
    
    @Test
    public void testUnmarshalUsingConstructor() throws SecurityException, NoSuchMethodException {
       
        assertEquals(new SubjectA("UNMARSHALLED_BY_METHOD"), binding.unmarshal("UNMARSHALLED_BY_METHOD:MARSHALLED_BY_METHOD"));
    }   
    
    @Test
    public void testMarshalUsingMethod() throws SecurityException, NoSuchMethodException {
        
        assertEquals("1:MARSHALLED_BY_A", binding.marshal(new SubjectA("1")));
    }
}
