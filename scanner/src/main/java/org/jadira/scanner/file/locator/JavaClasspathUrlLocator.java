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
package org.jadira.scanner.file.locator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jadira.scanner.core.api.Locator;
import org.jadira.scanner.core.exception.ClasspathAccessException;

/**
 * Uses the java.class.path system property to obtain a list of URLs that represent the
 * CLASSPATH
 */
public class JavaClasspathUrlLocator implements Locator<URL> {
    
	@Override
	public List<URL> locate() {

        List<URL> list = new ArrayList<URL>();
        String classpath = System.getProperty("java.class.path");
        StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);

        while (tokenizer.hasMoreTokens()) {
            
        	String path = tokenizer.nextToken();
            
            File fp = new File(path);
            if (!fp.exists()) {
                throw new ClasspathAccessException("File in java.class.path does not exist: " + fp);
            }
            
            try {
                list.add(fp.toURI().toURL());
            } catch (MalformedURLException e) {
            	throw new ClasspathAccessException("URL was invalid: " + fp.toURI().toString(), e);
            }
        }
        return list;
    }
}

