/*
 *  Copyright 2010, 2011 Chris Pheby
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
package org.jadira.bindings.core.utils.lang;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Helper used for working with the JDKs classloader API
 * @param <E> Subject of the enumeration to be wrapped
 */
public class IterableEnumeration<E> implements Iterable<E> {

    private final Enumeration<E> enumeration;

    /**
     * Creates a new instance
     * @param enumeration The enumeration to be wrapped
     */
    public IterableEnumeration(Enumeration<E> enumeration) {
        this.enumeration = enumeration;
    }

    /**
     * Returns an iterator wrapping the underlying enumeration
     */
    public Iterator<E> iterator() {

        return new Iterator<E>() {

            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            public E next() {
                return enumeration.nextElement();
            }

            public void remove() {
                throw new UnsupportedOperationException("IterableEnumeration does not support remove");
            }
        };
    }

    /**
     * Typesafe factory method for construction convenience
     * @param enumeration The enumeration to be wrapped with type-arg of E
     * @return An Iterable instance for subject type, E
     */
    public static <E> Iterable<E> wrapEnumeration(Enumeration<E> enumeration) {
        return new IterableEnumeration<E>(enumeration);
    }
}