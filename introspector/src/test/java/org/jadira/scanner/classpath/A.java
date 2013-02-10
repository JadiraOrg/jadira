/*
 *  Copyright 2012 Chris Pheby
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
package org.jadira.scanner.classpath;

import java.io.File;
import java.io.FilenameFilter;

public class A {

    A(String first, Integer second) {
        @SuppressWarnings("unused")
        String t;
    }

    public void doIt() {

        @SuppressWarnings("unused")
        @Deprecated
        String t;
        t = "" + Math.random();
        @SuppressWarnings("unused")
        Integer s;
    }

    public class B {
    }

    public void testAnon() {

        @SuppressWarnings("unused")
        FilenameFilter filter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return false;
            }

        };
    }
}
