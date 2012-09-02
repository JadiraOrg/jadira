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

import org.junit.Test;

public class FloatStringBindingTest {

    private static final FloatStringBinding BINDING = new FloatStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        assertEquals(new Float(0), BINDING.unmarshal("0"));
        assertEquals(new Float(1), BINDING.unmarshal("1"));
        assertEquals(new Float(3.4028235E38F), BINDING.unmarshal("" + Float.MAX_VALUE));
        assertEquals(new Float(1.4E-45), BINDING.unmarshal("" + Float.MIN_VALUE));
        
        assertEquals(new Float("0.0"), BINDING.unmarshal("0.0"));
        assertEquals(new Float("1.123"), BINDING.unmarshal("1.123"));
    }
    
    @Test
    public void testMarshal() {

        assertEquals("0.0", BINDING.marshal(0.0F));
        assertEquals("1.0", BINDING.marshal(1.0F));
        assertEquals("3.4028235E38", BINDING.marshal(Float.MAX_VALUE));
        assertEquals("1.4E-45", BINDING.marshal(Float.MIN_VALUE));
        
        assertEquals("0.0", BINDING.marshal(new Float("0.0")));
        assertEquals("1.123", BINDING.marshal(new Float("1.123")));
    }
}
