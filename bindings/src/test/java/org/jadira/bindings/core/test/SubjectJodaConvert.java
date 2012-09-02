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

import org.joda.convert.FromString;
import org.joda.convert.ToString;

public class SubjectJodaConvert {
    
    private String value;

    public SubjectJodaConvert(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @ToString
    public String marshalJoda() {
       
        return value + ":MARSHALLED_BY_JODA";
    }
    
    @FromString
    public static SubjectJodaConvert unmarshalJoda(String string) {
        return new SubjectJodaConvert(string.substring(0, string.lastIndexOf(':')));
    }
    
    @Override
    public int hashCode() {
        return 1 + value.hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        
        boolean result;
        if (other instanceof SubjectJodaConvert) {
            result = value.equals(((SubjectJodaConvert)other).value);
        } else {
            result = false;
        }
        return result;
    }
}