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

import org.jadira.bindings.core.general.unmarshaller.ConstructorFromUnmarshaller;
import org.jadira.bindings.core.test.SubjectA;
import org.junit.Test;

public class ConstructorFromUnmarshallerTest {

    @Test
    public void testUnmarshalUsingMethodA() throws SecurityException, NoSuchMethodException {
        
        Constructor<SubjectA> constructor = SubjectA.class.getConstructor(new Class[]{ String.class });
        ConstructorFromUnmarshaller<SubjectA, String> unmarshaller = new ConstructorFromUnmarshaller<SubjectA, String>(constructor);
        assertEquals(new SubjectA("UNMARSHALLED_BY_A"), unmarshaller.unmarshal("UNMARSHALLED_BY_A"));
    }    
}
