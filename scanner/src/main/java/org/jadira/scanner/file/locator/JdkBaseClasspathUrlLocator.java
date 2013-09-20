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

import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jadira.scanner.core.api.Locator;
import org.jadira.scanner.core.exception.ClasspathAccessException;
import org.jadira.scanner.core.helper.filenamefilter.JarFilenameFilter;

/**
 * Resolves a list of classpaths representing the core 'rt.jar' and any endorsed
 * @return Array of URLs from the Classpath
 */
public class JdkBaseClasspathUrlLocator implements Locator<URL> {

	private static final FilenameFilter JAR_FILENAME_FILTER = new JarFilenameFilter();
    
	private boolean includeRtJar;

	public JdkBaseClasspathUrlLocator() {
        this(true);
    }
	
	public JdkBaseClasspathUrlLocator(boolean includeRtJar) {
	    this.includeRtJar = includeRtJar;
	}
	
	@Override
	public List<URL> locate() {

        List<URL> classpaths = new ArrayList<URL>();

        String[] endorsedDirs = System.getProperty("java.endorsed.dirs").split(System.getProperty("path.separator"));
        processClasspathDefinition(classpaths, endorsedDirs);

        try {
            // Can't resolve using sun.boot.class.path - vendor specific
            if (includeRtJar) {
                URL javaClasspath = new java.io.File(System.getProperty("java.home") + System.getProperty("file.separator") + "lib" + System.getProperty("file.separator") + "rt.jar").toURI().toURL();
                classpaths.add(javaClasspath);
            }
        } catch (MalformedURLException e) {
            throw new ClasspathAccessException("Problem constructing Java classpath: " + e.getMessage(), e);
        }

        String[] extensionDirs = System.getProperty("java.ext.dirs").split(System.getProperty("path.separator"));
        processClasspathDefinition(classpaths, extensionDirs);


        return classpaths;
    }

    private static void processClasspathDefinition(List<URL> classpaths, String[] extensionDirs) {
        for (int i = 0; i < extensionDirs.length; i++) {
            java.io.File nextDir = new java.io.File(extensionDirs[i]);
            
            String[] jars = nextDir.list(JAR_FILENAME_FILTER);
            
            if (jars != null) {
                for (int jarIdx = 0; jarIdx < jars.length; jarIdx++) {
                    try {
                        URL nextJar = new URL(nextDir.toURI().toURL().toString() + jars[jarIdx]);
                        classpaths.add(nextJar);
                    } catch (MalformedURLException e) {
                        throw new ClasspathAccessException("Problem constructing Java extension classpath: " + e.getMessage(), e);
                    }
                }
            }
        }
    }
}

