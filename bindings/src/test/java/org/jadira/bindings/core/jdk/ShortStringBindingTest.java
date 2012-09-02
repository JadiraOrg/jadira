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

public class ShortStringBindingTest {

    private static final ShortStringBinding BINDING = new ShortStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        assertEquals(new Short((short)0).toString(), BINDING.unmarshal("0").toString());
        assertEquals(new Short((short)1).toString(), BINDING.unmarshal("1").toString());
        assertEquals(new Short((short)32767).toString(), BINDING.unmarshal("" + Short.MAX_VALUE).toString());
        assertEquals(new Short((short)-32768).toString(), BINDING.unmarshal("" + Short.MIN_VALUE).toString());
    }
    
    @Test
    public void testMarshal() {

        assertEquals("0", BINDING.marshal(new Short((short)0)));
        assertEquals("1", BINDING.marshal(new Short((short)1)));
        assertEquals("32767", BINDING.marshal(new Short(Short.MAX_VALUE)));
        assertEquals("-32768", BINDING.marshal(new Short(Short.MIN_VALUE)));
    }
}
