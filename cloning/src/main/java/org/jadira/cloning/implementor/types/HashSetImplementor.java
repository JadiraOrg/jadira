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

import java.util.HashSet;
import java.util.IdentityHashMap;

import org.jadira.cloning.api.CloneDriver;
import org.jadira.cloning.api.CloneImplementor;

/**
 * A Clone Implementor that can handle HashSet
 */
public class HashSetImplementor implements CloneImplementor {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> T clone(T obj, CloneDriver parentContext, IdentityHashMap<Object, Object> referencesToReuse) {
        
        final HashSet source = (HashSet)obj;
        
        final HashSet copy = new HashSet();
        for (final Object o : source) {
            final Object childCopy = parentContext.clone(o, parentContext, referencesToReuse);
            copy.add(childCopy);
        }
        return (T) copy;
    }

    @Override
    public boolean canClone(Class<?> clazz) {
        return HashSet.class.equals(clazz);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> T newInstance(Class<T> c) {
        if (canClone(c)) {
            return (T) new HashSet();
        } else {
            throw new IllegalStateException("Cannot create new instance for: " + c.getName());
        }
    }
}
