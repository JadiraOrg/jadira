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
package org.jadira.cloning.implementor.types;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jadira.cloning.api.CloneDriver;
import org.jadira.cloning.api.CloneImplementor;

/**
 * A Clone Implementor that can handle TreeMap
 */
public class TreeMapImplementor implements CloneImplementor {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> T clone(T obj, CloneDriver parentContext, IdentityHashMap<Object, Object> referencesToReuse) {
        
        final TreeMap<Object, Object> source = (TreeMap)obj;
        
        final TreeMap copy = new TreeMap(source.comparator());
        
        for (final Map.Entry e : source.entrySet()) {
            final Object key = parentContext.clone(e.getKey(), parentContext, referencesToReuse);
            final Object value = parentContext.clone(e.getValue(), parentContext, referencesToReuse);
            
            copy.put(key, value);
        }
        return (T) copy;
    }


    @Override
    public boolean canClone(Class<?> clazz) {
        return TreeMap.class.equals(clazz);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> T newInstance(Class<T> c) {
        if (canClone(c)) {
            return (T) new TreeMap();
        } else {
            throw new IllegalStateException("Cannot create new instance for: " + c.getName());
        }
    }
}
