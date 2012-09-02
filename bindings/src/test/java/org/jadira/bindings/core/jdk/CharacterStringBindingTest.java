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

public class CharacterStringBindingTest {

    private static final CharacterStringBinding BINDING = new CharacterStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        assertEquals(new Character('a'), BINDING.unmarshal("a"));
        assertEquals(new Character('\u00df'), BINDING.unmarshal("\u00df"));
    }
    
    @Test
    public void testMarshal() {

        assertEquals("a", BINDING.marshal(new Character('a')));
        assertEquals("\u00df", BINDING.marshal(new Character('\u00df')));
    }
}
