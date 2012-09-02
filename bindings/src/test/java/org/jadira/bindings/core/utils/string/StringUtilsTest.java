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
package org.jadira.bindings.core.utils.string;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsTest {
    
    @Test
    public void testRemoveWhitespace() {
        
        assertEquals("Helloworld", StringUtils.removeWhitespace("Hello world"));
        assertEquals("Hello\uD840\uDC08", StringUtils.removeWhitespace("Hello  \uD840\uDC08"));
        assertEquals("Hello\uD840\uDC08", StringUtils.removeWhitespace(" Hello  \uD840\uDC08 "));
        assertEquals("Hello\uD840\uDC08", StringUtils.removeWhitespace(" Hello  \n \uD840\uDC08 "));
    }
}
