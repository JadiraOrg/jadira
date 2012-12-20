/*
 *  Copyright 2012 Chris Pheby
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
package org.jadira.scanner.api;

import java.net.URL;

/**
 * Instances of classes that implement this interface are used to filter URLs. Example use is for filtering the
 * classpath entries associated with a resource resolver to those jars containing a marker configuration file (e.g.
 * beans.xml or swift.xml)
 */
public interface UrlFilter {
	
    /**
     * Tests if a specified url should be included in a url list.
     * @param url the directory in which the file was found.
     * @return <code>true</code> if and only if the URL should be included in the list; <code>false</code> otherwise.
     */
    boolean accept(URL url);
}