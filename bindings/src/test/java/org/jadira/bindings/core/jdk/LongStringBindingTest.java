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

public class LongStringBindingTest {

    private static final LongStringBinding BINDING = new LongStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        assertEquals(new Long(0).toString(), BINDING.unmarshal("0").toString());
        assertEquals(new Long(1).toString(), BINDING.unmarshal("1").toString());
        assertEquals(new Long(9223372036854775807L).toString(), BINDING.unmarshal("" + Long.MAX_VALUE).toString());
        assertEquals(new Long(-9223372036854775808L).toString(), BINDING.unmarshal("" + Long.MIN_VALUE).toString());
    }
    
    @Test
    public void testMarshal() {

        assertEquals("0", BINDING.marshal(new Long(0)));
        assertEquals("1", BINDING.marshal(new Long(1)));
        assertEquals("9223372036854775807", BINDING.marshal(new Long(Long.MAX_VALUE)));
        assertEquals("-9223372036854775808", BINDING.marshal(new Long(Long.MIN_VALUE)));
    }
}
