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

import java.util.Locale;

import org.junit.Test;

public class LocaleStringBindingTest {

    private static final LocaleStringBinding BINDING = new LocaleStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        assertEquals(new Locale("en"), BINDING.unmarshal("en"));
        assertEquals(new Locale("en","GB"), BINDING.unmarshal("en_GB"));
        assertEquals(new Locale("en","GB","ck"), BINDING.unmarshal("en_gb_ck"));
    }
    
    @Test
    public void testMarshal() {

        assertEquals("en", BINDING.marshal(new Locale("en")));
        assertEquals("en_GB", BINDING.marshal(new Locale("en","GB")));
        assertEquals("en_GB_ck", BINDING.marshal(new Locale("en","GB","ck")));
    }
}
