package org.jadira.bindings.core.utils.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

public class IterableEnumerationTest {

    private static final Vector<String> ELEMENTS = new Vector<String>();
    
    @BeforeClass
    public static void setup() {
        ELEMENTS.add("FOO");
        ELEMENTS.add("BAR");
        ELEMENTS.add("THE_END");
    }
    
    @Test
    public void testIterableEnumeration() {
        
        Iterable<String> testIterable = new IterableEnumeration<String>(ELEMENTS.elements());
        Iterator<String> myIter = testIterable.iterator();
        
        testValues(myIter);

        testIterable = IterableEnumeration.wrapEnumeration(ELEMENTS.elements());
        myIter = testIterable.iterator();
        
        testValues(myIter);

    }

    private void testValues(Iterator<String> myIter) {
        
        assertTrue(myIter.hasNext());
        assertEquals("FOO", myIter.next());
        assertTrue(myIter.hasNext());
        assertEquals("BAR", myIter.next());
        assertTrue(myIter.hasNext());
        assertEquals("THE_END", myIter.next());
        assertFalse(myIter.hasNext());
        try {
            myIter.next();
            fail("Should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }
}
