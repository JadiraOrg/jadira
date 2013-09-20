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
import org.jadira.scanner.core.exception.FileAccessException;
import org.jadira.scanner.core.utils.lang.IterableEnumeration;
import org.jadira.scanner.core.utils.reflection.ClassLoaderUtils;

/**
 * Matches any Jars with a marker file indicated
 */
public class MarkerFileClasspathUrlLocator implements Locator<URL> {

	private List<String> paths;

	private final ClassLoader[] classLoaders;
	
	public MarkerFileClasspathUrlLocator(String markerFilePath, ClassLoader... classLoaders) {
		this.paths = new ArrayList<String>(1);
		paths.add(asNormalizedName(markerFilePath));
		this.classLoaders = ClassLoaderUtils.getClassLoaders(classLoaders);
	}
	
	public MarkerFileClasspathUrlLocator(List<String> markerFilePaths, ClassLoader... classLoaders) {
        this.paths = new ArrayList<String>(markerFilePaths.size());
        for (String next : markerFilePaths) {
            paths.add(asNormalizedName(next));
        }
        this.classLoaders = ClassLoaderUtils.getClassLoaders(classLoaders);
	}
	
	@Override
	public List<URL> locate() {

        List<URL> list = new ArrayList<URL>();
        
        for (ClassLoader classLoader : classLoaders) {
            try {
            	for (String nextPath : paths) {
    	            for (URL nextResourceMatchedUrl : new IterableEnumeration<URL>(classLoader.getResources(nextPath))) {
    	
    	                String deploymentArchiveRoot = determineClasspathRootForResource(nextPath, nextResourceMatchedUrl);
    	                
                        File fp = new File(deploymentArchiveRoot);
                        
                        if (!fp.exists()) {
                            throw new FileAccessException("File unexpectedly does not exist: " + fp);
                        }
                        
                        try {
                            list.add(fp.toURI().toURL());
                        } catch (MalformedURLException e) {
                        	throw new FileAccessException("Filepath unexpectedly malformed: " + fp.getPath(), e);
                        }
    	            }
            	}
            } catch (IOException e) {
            	throw new FileAccessException("Problem resolving deployment archives: " + e.getMessage(), e);
            }
        }
		
        return list;
    }
	
    private String determineClasspathRootForResource(String nextResource, URL nextResourceMatchedUrl) {

        String nextResourceMatchedPathName = nextResourceMatchedUrl.getFile();
        try {
            nextResourceMatchedPathName = URLDecoder.decode(nextResourceMatchedPathName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Never thrown for UTF-8
            throw new FileAccessException("Exception thrown for Encoding when not expected: " + e.getMessage(), e);
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
    
    private static String asNormalizedName(String name) {
     
        String result = name;        
        if (result != null) {
            result = result.replace("\\", "/");
            if (result.startsWith("/")) {
                result = result.substring(1);
            }
        }
        return result;
    }
}
