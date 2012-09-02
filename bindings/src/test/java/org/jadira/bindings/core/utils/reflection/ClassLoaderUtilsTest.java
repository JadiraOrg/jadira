package org.jadira.bindings.core.utils.reflection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClassLoaderUtilsTest {
    
    @Test
    public void testGetClassLoader() {

        assertEquals("sun.misc.Launcher$AppClassLoader", ClassLoaderUtils.getClassLoader().getClass().getName());
    }
}
