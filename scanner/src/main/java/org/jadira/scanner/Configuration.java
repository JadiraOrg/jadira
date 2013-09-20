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
package org.jadira.scanner;

import java.net.URL;
import java.util.List;

import org.jadira.scanner.core.api.Filter;
import org.jadira.scanner.core.api.Locator;

public interface Configuration {

    /** 
     * URLs to be scanned 
     */
    List<URL> getUrls();

    /** 
     * Locators used to construct additional URLs to be scanned 
     */
    List<Locator<URL>> getLocators();

    /**
     * ClassLoaders to be used for resolution, if none are set the context ClassLoader or a statically defined
     * ClassLoader will be used
     */
    List<ClassLoader> getClassLoaders();

    List<Filter<?>> getFilters();
}