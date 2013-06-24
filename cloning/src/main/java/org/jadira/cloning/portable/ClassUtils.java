/*
 *  Copyright 2013 Chris Pheby
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
package org.jadira.cloning.portable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility methods for inspecting and interpreting classes
 */
public final class ClassUtils {
    
    private ClassUtils() {
    }

    public static boolean isJdkImmutable(Class<?> type) {
        
    	if (Class.class == type) {
    		return true;
    	}
        if (String.class == type) {
            return true;
        }
        if (BigInteger.class == type) {
            return true;
        }
        if (BigDecimal.class == type) {
            return true;
        }
        if (URL.class == type) {
            return true;
        }
        if (UUID.class == type) {
            return true;
        }
        if (URI.class == type) {
            return true;
        }
        if (Pattern.class == type) {
            return true;
        }
        return false;
    }

    public static boolean isWrapper(Class<?> type) {

        if (Boolean.class == type) {
            return true;
        } 
        if (Byte.class == type) {
            return true;
        } 
        if (Character.class == type) {
            return true;
        } 
        if (Short.class == type) {
            return true;
        } 
        if (Integer.class == type) {
            return true;
        } 
        if (Long.class == type) {
            return true;
        }
        if (Float.class == type) {
            return true;
        } 
        if (Double.class == type) {
            return true;
        }
        return false;
    }
    
    public static Field[] collectFields(Class<?> c) {
        
        Set<Field> fields = new HashSet<Field>();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) == 0) {
                    fields.add(f);
                    f.setAccessible(true);
                }
            }
            c = c.getSuperclass();
        }
        return fields.toArray(new Field[]{});
    }
}
