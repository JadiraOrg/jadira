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

import java.math.BigDecimal;

import org.junit.Test;

public class BigDecimalStringBindingTest {

    private static final BigDecimalStringBinding BINDING = new BigDecimalStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        assertEquals(new BigDecimal(0).toString(), BINDING.unmarshal("0").toString());
        assertEquals(new BigDecimal(1).toString(), BINDING.unmarshal("1").toString());
        assertEquals(new BigDecimal(9223372036854775807L).toString(), BINDING.unmarshal("" + Long.MAX_VALUE).toString());
        assertEquals(new BigDecimal(-9223372036854775808L).toString(), BINDING.unmarshal("" + Long.MIN_VALUE).toString());
        assertEquals(new BigDecimal("9223372036854775808").toString(), BINDING.unmarshal("9223372036854775808").toString());
        assertEquals(new BigDecimal("-9223372036854775809").toString(), BINDING.unmarshal("-9223372036854775809").toString());
        
        assertEquals(new BigDecimal("0.0").toString(), BINDING.unmarshal("0.0").toString());
        assertEquals(new BigDecimal("1.123").toString(), BINDING.unmarshal("1.123").toString());
        assertEquals(new BigDecimal("9223372036854775807.034").toString(), BINDING.unmarshal("9223372036854775807.034").toString());
        assertEquals(new BigDecimal("-9223372036854775808.034").toString(), BINDING.unmarshal("-9223372036854775808.034").toString());
        assertEquals(new BigDecimal("9223372036854775808.234234324324324").toString(), BINDING.unmarshal("9223372036854775808.234234324324324").toString());
        assertEquals(new BigDecimal("-9223372036854775809.234234324324324").toString(), BINDING.unmarshal("-9223372036854775809.234234324324324").toString());
    }
    
    @Test
    public void testMarshal() {

        assertEquals("0", BINDING.marshal(new BigDecimal(0)));
        assertEquals("1", BINDING.marshal(new BigDecimal(1)));
        assertEquals("9223372036854775808", BINDING.marshal(new BigDecimal(Long.MAX_VALUE).add(BigDecimal.ONE)));
        assertEquals("-9223372036854775809", BINDING.marshal(new BigDecimal(Long.MIN_VALUE).subtract(BigDecimal.ONE)));
        
        assertEquals("0.0", BINDING.marshal(new BigDecimal("0.0")));
        assertEquals("1.123", BINDING.marshal(new BigDecimal("1.123")));
        assertEquals("9223372036854775807.034", BINDING.marshal(new BigDecimal("9223372036854775807.034")));
        assertEquals("-9223372036854775808.034", BINDING.marshal(new BigDecimal("-9223372036854775808.034")));
        assertEquals("9223372036854775808.234234324324324", BINDING.marshal(new BigDecimal("9223372036854775808.234234324324324")));
        assertEquals("-9223372036854775809.234234324324324", BINDING.marshal(new BigDecimal("-9223372036854775809.234234324324324")));
    }
}
