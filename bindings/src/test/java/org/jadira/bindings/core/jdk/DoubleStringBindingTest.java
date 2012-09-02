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

public class DoubleStringBindingTest {

    private static final DoubleStringBinding BINDING = new DoubleStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        assertEquals(new Double(0).toString(), BINDING.unmarshal("0").toString());
        assertEquals(new Double(1).toString(), BINDING.unmarshal("1").toString());
        assertEquals(new Double(9223372036854775807D), BINDING.unmarshal(new Double(9.223372036854776E18).toString()));
        assertEquals(new Double(-9223372036854775808D).toString(), BINDING.unmarshal("" + Long.MIN_VALUE).toString());
        assertEquals(new Double("9223372036854775808").toString(), BINDING.unmarshal("9223372036854775808").toString());
        assertEquals(new Double("-9223372036854775809").toString(), BINDING.unmarshal("-9223372036854775809").toString());
        
        assertEquals(new Double("0.0").toString(), BINDING.unmarshal("0.0").toString());
        assertEquals(new Double("1.123").toString(), BINDING.unmarshal("1.123").toString());
        assertEquals(new Double("9.223372036854776E18").toString(), BINDING.unmarshal("9223372036854775807.034").toString());
        assertEquals(new Double("-9.223372036854776E18").toString(), BINDING.unmarshal("-9223372036854775808.034").toString());
        assertEquals(new Double("9.223372036854776E18").toString(), BINDING.unmarshal("9223372036854775808.234234324324324").toString());
        assertEquals(new Double("-9.223372036854776E18").toString(), BINDING.unmarshal("-9223372036854775809.234234324324324").toString());
    }
    
    @Test
    public void testMarshal() {

        assertEquals("0.0", BINDING.marshal(0.0D));
        assertEquals("1.0", BINDING.marshal(1.0D));
        assertEquals("9.223372036854776E18", BINDING.marshal(new Double(Long.MAX_VALUE)));
        assertEquals("-9.223372036854776E18", BINDING.marshal(new Double(Long.MIN_VALUE)));
        
        assertEquals("0.0", BINDING.marshal(new Double("0.0")));
        assertEquals("1.123", BINDING.marshal(new Double("1.123")));
        assertEquals("9.223372036854776E18", BINDING.marshal(new Double("9223372036854775807.034")));
        assertEquals("-9.223372036854776E18", BINDING.marshal(new Double("-9223372036854775808.034")));
        assertEquals("9.223372036854776E18", BINDING.marshal(new Double("9223372036854775808.234234324324324")));
        assertEquals("-9.223372036854776E18", BINDING.marshal(new Double("-9223372036854775809.234234324324324")));
    }
}
