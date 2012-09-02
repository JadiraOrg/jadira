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

import java.util.Currency;

import org.junit.Test;

public class CurrencyStringBindingTest {

    private static final CurrencyStringBinding BINDING = new CurrencyStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        assertEquals(Currency.getInstance("USD"), BINDING.unmarshal("USD"));
        assertEquals(Currency.getInstance("GBP"), BINDING.unmarshal("GBP"));
        assertEquals(Currency.getInstance("AUD"), BINDING.unmarshal("AUD"));
    }
    
    @Test
    public void testMarshal() {

        assertEquals("USD", BINDING.marshal(Currency.getInstance("USD")));
        assertEquals("GBP", BINDING.marshal(Currency.getInstance("GBP")));
        assertEquals("AUD", BINDING.marshal(Currency.getInstance("AUD")));
    }
}
