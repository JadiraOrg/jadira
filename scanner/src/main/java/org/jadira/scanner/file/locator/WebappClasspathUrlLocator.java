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
import java.util.Set;

import javax.servlet.ServletContext;

import org.jadira.scanner.core.api.Locator;
import org.jadira.scanner.core.exception.ClasspathAccessException;

/**
 * Utilities for resolving urls against the classpath of a Web Archive
 */
public class WebappClasspathUrlLocator implements Locator<URL> {

	private final ServletContext servletContext;

	public WebappClasspathUrlLocator(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	@Override
	public List<URL> locate() {

        List<URL> list = new ArrayList<URL>();
        @SuppressWarnings("unchecked") Set<String> libJars = servletContext.getResourcePaths("/WEB-INF/lib");
        for (String jar : libJars) {
            try {
                list.add(servletContext.getResource(jar));
            } catch (MalformedURLException e) {
                throw new ClasspathAccessException(e);
            }
        }
        list.add(findWebInfClassesPath(servletContext));
        return list;
    }

    /**
     * Find the URL pointing to "/WEB-INF/classes" This method may not work in conjunction with
     * IteratorFactory if your servlet container does not extract the /WEB-INF/classes into a real
     * file-based directory
     * @param servletContext
     * @return null if cannot determin /WEB-INF/classes
     */
    private static URL findWebInfClassesPath(ServletContext servletContext) {
        String path = servletContext.getRealPath("/WEB-INF/classes");
        if (path == null) {
            return null;
        }
        File fp = new File(path);
        if (!fp.exists()) {
            return null;
        }
        try {
            return fp.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new ClasspathAccessException(e);
        }
    }
}