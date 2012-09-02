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
package org.jadira.bindings.core.cdi;

import javax.annotation.PostConstruct;

import org.jadira.bindings.core.annotation.From;
import org.jadira.bindings.core.annotation.To;

public class HelloWorld {
    
    private String text = "Hello World!";

    @From
    public HelloWorld(String text) {
        this.text = text;
    }
    
    public HelloWorld() {
    }

    @PostConstruct
    public void initialize() {
        System.out.println(this.getClass().getSimpleName() + " was constructed");
    }

    @To
    public String getText() {
        return text;
    }
}