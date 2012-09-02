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

import java.io.File;

import org.junit.Test;

public class FileStringBindingTest {

    private static final FileStringBinding BINDING = new FileStringBinding();
    
    @Test
    public void testUnmarshal() {
     
        assertEquals(new File("/tmp/Atomic"), BINDING.unmarshal("/tmp/Atomic"));
    }
    
    @Test
    public void testMarshal() {

        assertEquals(System.getProperty("file.separator") + "tmp" + System.getProperty("file.separator") + "Atomic", BINDING.marshal(new File("/tmp/Atomic")));
    }
}
