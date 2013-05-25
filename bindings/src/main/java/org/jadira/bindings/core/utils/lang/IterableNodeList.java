/*
 *  Copyright 2011 Chris Pheby
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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Helper used for working with the JDKs NodeList API
 */
public class IterableNodeList implements Iterable<Node> {

    private final NodeList nodeList;

    /**
     * Creates a new instance
     * @param nodeList The NodeList to be wrapped
     */
    public IterableNodeList(NodeList nodeList) {
        this.nodeList = nodeList;
    }

    /**
     * Returns an iterator wrapping the underlying enumeration
     */
    public Iterator<Node> iterator() {

        return new Iterator<Node>() {
            private int currentIndex = 0;
            
            public boolean hasNext() {
                return currentIndex < nodeList.getLength();
            }
            
            public Node next() {
                Node next = nodeList.item(currentIndex);
                currentIndex++;
                if (next == null) {
                    throw new NoSuchElementException("No element found for index: " + currentIndex);
                }
                return next;
            }
            
            public void remove() {
                throw new UnsupportedOperationException("IterableNodeList does not support remove");
            }
        };
    }
}
