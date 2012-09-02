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

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

public class AtomicLongStringBindingTest {

    private static final AtomicLongStringBinding BINDING = new AtomicLongStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        assertEquals(new AtomicLong(0).toString(), BINDING.unmarshal("0").toString());
        assertEquals(new AtomicLong(1).toString(), BINDING.unmarshal("1").toString());
        assertEquals(new AtomicLong(9223372036854775807L).toString(), BINDING.unmarshal("" + Long.MAX_VALUE).toString());
        assertEquals(new AtomicLong(-9223372036854775808L).toString(), BINDING.unmarshal("" + Long.MIN_VALUE).toString());
    }
    
    @Test
    public void testMarshal() {

        assertEquals("0", BINDING.marshal(new AtomicLong(0)));
        assertEquals("1", BINDING.marshal(new AtomicLong(1)));
        assertEquals("9223372036854775807", BINDING.marshal(new AtomicLong(Long.MAX_VALUE)));
        assertEquals("-9223372036854775808", BINDING.marshal(new AtomicLong(Long.MIN_VALUE)));
    }
}
