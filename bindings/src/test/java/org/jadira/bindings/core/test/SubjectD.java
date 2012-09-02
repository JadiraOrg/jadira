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
package org.jadira.bindings.core.test;

import org.jadira.bindings.core.annotation.From;
import org.jadira.bindings.core.annotation.To;
import org.joda.convert.FromString;

public class SubjectD {
    
    private String value;

    public SubjectD(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @To @Narrow
    public String marshalMethodA() {
       
        return value + ":MARSHALLED_BY_A";
    }
    
    @To
    public static String marshalMethodB(SubjectD subject) {
        
        return subject.value + ":MARSHALLED_BY_B";
    }
    
    @From
    public static SubjectD unmarshalMethodA(String string) {
        return new SubjectD(string.substring(0, string.lastIndexOf(':')));
    }

    @FromString @Narrow
    public static SubjectD unmarshalNarrow(String string) {
        return new SubjectD(string);
    }
    
    @Override
    public int hashCode() {
        return 1 + value.hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        
        boolean result;
        if (other instanceof SubjectD) {
            result = value.equals(((SubjectD)other).value);
        } else {
            result = false;
        }
        return result;
    }
}