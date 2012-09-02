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

import org.jadira.bindings.core.general.binding.JodaConvertStringBinding;
import org.jadira.bindings.core.test.SubjectJodaConvert;
import org.joda.convert.StringConvert;
import org.junit.Test;

public class JodaConvertStringBindingTest {

    @Test
    public void testUnmarshalUsingJoda() throws SecurityException, NoSuchMethodException {
        
        JodaConvertStringBinding<SubjectJodaConvert> unmarshaller = new JodaConvertStringBinding<SubjectJodaConvert>(SubjectJodaConvert.class, StringConvert.INSTANCE.findConverter(SubjectJodaConvert.class));
        assertEquals(new SubjectJodaConvert("UNMARSHALLED_BY_JODA"), unmarshaller.unmarshal("UNMARSHALLED_BY_JODA:MARSHALLED_BY_JODA"));
    }   
    
    @Test
    public void testMarshalUsingJoda() throws SecurityException, NoSuchMethodException {
        
        JodaConvertStringBinding<SubjectJodaConvert> marshaller = new JodaConvertStringBinding<SubjectJodaConvert>(SubjectJodaConvert.class, StringConvert.INSTANCE.findConverter(SubjectJodaConvert.class));
        assertEquals("1:MARSHALLED_BY_JODA", marshaller.marshal(new SubjectJodaConvert("1")));
    }
}
