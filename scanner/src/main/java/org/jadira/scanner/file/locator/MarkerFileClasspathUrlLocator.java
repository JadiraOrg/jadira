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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.jadira.scanner.core.api.Locator;
import org.jadira.scanner.core.utils.lang.IterableEnumeration;
import org.jadira.scanner.core.utils.reflection.ClassLoaderUtils;

/**
 * Matches any Jars with a marker file indicated
 */
public class MarkerFileClasspathUrlLocator implements Locator<URL> {

	private List<String> paths;

	public MarkerFileClasspathUrlLocator(String markerFilePath) {
		this.paths = new ArrayList<String>(1);
		paths.add(markerFilePath);
	}
	
	public MarkerFileClasspathUrlLocator(List<String> markerFilePaths) {
		this.paths = markerFilePaths;
	}
	
	@Override
	public List<URL> locate() {

        List<URL> list = new ArrayList<URL>();
        
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader();
        try {
        	for (String nextPath : paths) {
	            for (URL nextResourceMatchedUrl : new IterableEnumeration<URL>(classLoader.getResources(nextPath))) {
	
	                String deploymentArchiveRoot = determineClasspathRootForResource(nextPath, nextResourceMatchedUrl);
	                
                    File fp = new File(deploymentArchiveRoot);
                    if (!fp.exists())
                        throw new RuntimeException("File unexpectedly does not exist: " + fp);
                    try {
                        list.add(fp.toURI().toURL());
                    } catch (MalformedURLException e) {
                    	throw new RuntimeException("Filepath unexpectedly malformed: " + fp.getPath(), e);
                    }
	            }
        	}
        } catch (IOException e) {
        	throw new RuntimeException("Problem resolving deployment archives: " + e.getMessage(), e);
        }
		
        return list;
    }
	
    private String determineClasspathRootForResource(String nextResource, URL nextResourceMatchedUrl) {

        String nextResourceMatchedPathName = nextResourceMatchedUrl.getFile();
        try {
            nextResourceMatchedPathName = URLDecoder.decode(nextResourceMatchedPathName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Never thrown for UTF-8
            throw new RuntimeException("Exception thrown for Encoding when not expected: " + e.getMessage(), e);
        }

        // Reformat file urls to remove file: prefix
        if (nextResourceMatchedPathName.startsWith("file:")) {
            nextResourceMatchedPathName = nextResourceMatchedPathName.substring(5);
        }

        // Chomp archive name if an archive
        if (nextResourceMatchedPathName.indexOf('!') > 0) {
            nextResourceMatchedPathName = nextResourceMatchedPathName.substring(0, nextResourceMatchedPathName.indexOf('!'));
        } else {
            File indicatedResource = new File(nextResourceMatchedPathName);

            // Traverse to classpath root relative to the original matching resource
            int pathDepth = nextResource.replaceAll("[^/]", "").length();
            for (int i = 0; i < pathDepth; i++) {
                indicatedResource = indicatedResource.getParentFile();
            }
            nextResourceMatchedPathName = indicatedResource.getParent();
        }

        return nextResourceMatchedPathName;
    }
}
