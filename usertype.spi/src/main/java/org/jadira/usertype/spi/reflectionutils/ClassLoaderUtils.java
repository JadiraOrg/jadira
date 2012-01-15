/*
 *  Copyright 2010, 2011 Christopher Pheby
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
package org.jadira.usertype.spi.reflectionutils;

/**
 * Utility methods related to {@link ClassLoader} resolution and the like
 */
public class ClassLoaderUtils {

    private ClassLoaderUtils() {
    }
    
    /**
     * Gets an appropriate {@link ClassLoader} for the current thread
     * @return
     */
    public static final ClassLoader getClassLoader() {

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        if (cl == null) {
            cl = ClassLoaderUtils.class.getClassLoader();
        }

        return cl;
    }
}
