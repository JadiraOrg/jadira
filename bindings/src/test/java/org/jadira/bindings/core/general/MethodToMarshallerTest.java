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

import org.jadira.bindings.core.general.marshaller.MethodToMarshaller;
import org.jadira.bindings.core.test.SubjectA;
import org.jadira.bindings.core.test.SubjectB;
import org.junit.Test;

public class MethodToMarshallerTest {

    @Test
    public void testMarshalUsingMethodA() throws SecurityException, NoSuchMethodException {
        
        Method method = SubjectA.class.getMethod("marshalMethodA", new Class[]{});
        MethodToMarshaller<SubjectA, String> marshaller = new MethodToMarshaller<SubjectA, String>(SubjectA.class, String.class, method);
        assertEquals("1:MARSHALLED_BY_A", marshaller.marshal(new SubjectA("1")));
    }
    
    @Test
    public void testMarshalUsingMethodB() throws SecurityException, NoSuchMethodException {
        
        Method method = SubjectA.class.getMethod("marshalMethodB", new Class[]{ SubjectA.class });
        MethodToMarshaller<SubjectA, String> marshaller = new MethodToMarshaller<SubjectA, String>(SubjectA.class, String.class, method);
        assertEquals("2:MARSHALLED_BY_B", marshaller.marshal(new SubjectA("2")));
    }
    
    @Test
    public void testMarshalUsingMethodC() throws SecurityException, NoSuchMethodException {
        
        Method method = SubjectB.class.getMethod("marshalMethodC", new Class[]{ SubjectA.class });
        MethodToMarshaller<SubjectA, String> marshaller = new MethodToMarshaller<SubjectA, String>(SubjectA.class, String.class, method);
        assertEquals("3:MARSHALLED_BY_C", marshaller.marshal(new SubjectA("3")));
    }
}
