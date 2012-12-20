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
package org.jadira.scanner.resolver;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang.ArrayUtils;
import org.jadira.scanner.api.UrlFilter;
import org.jadira.scanner.api.UrlLocator;
import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.filenamefilter.AntPathFilter;
import org.jadira.scanner.helper.FileUtils;
import org.jadira.scanner.urllocator.JdkBaseClasspathUrlLocator;
import org.jadira.scanner.urllocator.WebappClasspathUrlLocator;

public class FileResolver {

    private URL[] classpaths;

    private static final URL[] JDK_BASE_CLASSPATH_JARS = new JdkBaseClasspathUrlLocator().locate();
    
    protected FileResolver(URL... classpaths) {
        this.classpaths = (URL[]) ArrayUtils.addAll(JDK_BASE_CLASSPATH_JARS, classpaths);
    }

    protected FileResolver(UrlFilter filter, URL... classpaths) {
        URL[] myClasspaths = (URL[]) ArrayUtils.addAll(JDK_BASE_CLASSPATH_JARS, classpaths);
        List<URL> acceptedUrls = new ArrayList<URL>();
        for (URL url : myClasspaths) {
            if (filter.accept(url)) {
                acceptedUrls.add(url);
            }
        }
        this.classpaths = acceptedUrls.toArray(new URL[acceptedUrls.size()]);
    }

    public static final FileResolver getResolver(URL... classpaths) {
        return new FileResolver(classpaths);
    }


    public static final FileResolver getResolver(UrlFilter filter, URL... classpaths) {
        return new FileResolver(filter, classpaths);
    }
    
    public static final FileResolver getResolver(UrlLocator... locators) {
    	
        List<URL> urls = new ArrayList<URL>();
    	for (UrlLocator next : locators) {
    		URL[] locatedUrls = next.locate();
    		if (locatedUrls != null) {
    			for (int i = 0; i < locatedUrls.length; i++) {
    				urls.add(locatedUrls[i]);
    			}
    		}
    		
    	}
        return new FileResolver(urls.toArray(new URL[urls.size()]));
    }

    public static final FileResolver getResolver(UrlFilter filter, UrlLocator... locators) {

        List<URL> urls = new ArrayList<URL>();
    	for (UrlLocator next : locators) {
    		URL[] locatedUrls = next.locate();
    		if (locatedUrls != null) {
    			for (int i = 0; i < locatedUrls.length; i++) {
    				urls.add(locatedUrls[i]);
    			}
    		}
    		
    	}
        return new FileResolver(filter, urls.toArray(new URL[urls.size()]));
    }

    public static final FileResolver getResolver(ServletContext servletContext) {
        return new FileResolver(new WebappClasspathUrlLocator(servletContext).locate());
    }

    public static final FileResolver getResolver(UrlFilter filter, ServletContext servletContext) {
        return new FileResolver(new WebappClasspathUrlLocator(servletContext).locate());
    }

    public List<File> findFiles(String path) throws ClasspathAccessException {

        final List<File> files;

        AntPathFilter antPathMatcher = new AntPathFilter(path);
        if (antPathMatcher.isPatterned()) {
            files = findFilesForPatternPath(path);
        } else {
            files = findFilesForActualPath(path);
        }
        return files;
    }
    
    public List<File> findFilesForPatternPath(String pattern) throws ClasspathAccessException {

        final List<File> files = new ArrayList<File>();
        for (URL url : classpaths) {

            File parentFile = FileUtils.getFileFromURL(url);

            AntPathFilter antPathMatcher = new AntPathFilter(pattern);
            if(antPathMatcher.match(AntPathFilter.PATH_SEPARATOR)
                    || antPathMatcher.match("")) {
                files.add(parentFile);
            } else {
                findFilesForPatternRecursively(pattern, files, parentFile, parentFile);
            }
        }
        return files;
    }

    public List<File> findFilesForActualPath(String path) {

        final List<File> files = new ArrayList<File>();
        for (URL url : classpaths) {
            File nextFile = FileUtils.getFileForPathName(path, url);
            if ((nextFile != null) && nextFile.isFile()) {
                files.add(nextFile);
            }
        }
        return files;
    }
    
    private void findFilesForPatternRecursively(String pattern, final List<File> resultsHolder, File root, File currentParent) {

        if (currentParent.isDirectory()) {
            File[] childFiles = currentParent.listFiles();
            for (File next : childFiles) {
                String path = next.getPath().substring(root.getPath().length());
                if(next.isDirectory() && (!path.endsWith(AntPathFilter.PATH_SEPARATOR))) {
                    path = path + AntPathFilter.PATH_SEPARATOR;
                }
                AntPathFilter antPathMatcher = new AntPathFilter(pattern);
                if(antPathMatcher.match(path)) {
                    resultsHolder.add(next);
                } else if(antPathMatcher.matchStart(path)) {
                    findFilesForPatternRecursively(pattern, resultsHolder, root, currentParent);
                }
            }
        }
    }
}