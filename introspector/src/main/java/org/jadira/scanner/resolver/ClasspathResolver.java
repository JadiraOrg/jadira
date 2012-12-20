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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jadira.scanner.api.UrlFilter;
import org.jadira.scanner.api.UrlLocator;
import org.jadira.scanner.urllocator.JdkBaseClasspathUrlLocator;
import org.jadira.scanner.urllocator.WebappClasspathUrlLocator;

public class ClasspathResolver {

    private URL[] classpaths;

    private static final URL[] JDK_BASE_CLASSPATH_JARS = new JdkBaseClasspathUrlLocator().locate();
    
    protected ClasspathResolver(URL... classpaths) {
    	this.classpaths = (URL[]) ArrayUtils.addAll(JDK_BASE_CLASSPATH_JARS, classpaths);
    }

    protected ClasspathResolver(UrlFilter filter, URL... classpaths) {
        URL[] myClasspaths = (URL[]) ArrayUtils.addAll(JDK_BASE_CLASSPATH_JARS, classpaths);
        List<URL> acceptedUrls = new ArrayList<URL>();
        for (URL url : myClasspaths) {
            if (filter.accept(url)) {
                acceptedUrls.add(url);
            }
        }
        this.classpaths = acceptedUrls.toArray(new URL[acceptedUrls.size()]);
    }

    public static final ClasspathResolver getResolver(URL... classpaths) {
        return new ClasspathResolver(classpaths);
    }

    public static final ClasspathResolver getResolver(UrlFilter filter, URL... classpaths) {
        return new ClasspathResolver(filter, classpaths);
    }
    
    public static final ClasspathResolver getResolver(UrlLocator... locators) {
    	
        List<URL> urls = new ArrayList<URL>();
    	for (UrlLocator next : locators) {
    		URL[] locatedUrls = next.locate();
    		if (locatedUrls != null) {
    			for (int i = 0; i < locatedUrls.length; i++) {
    				urls.add(locatedUrls[i]);
    			}
    		}
    		
    	}
        return new ClasspathResolver(urls.toArray(new URL[urls.size()]));
    }

    public static final ClasspathResolver getResolver(UrlFilter filter, UrlLocator... locators) {

        List<URL> urls = new ArrayList<URL>();
    	for (UrlLocator next : locators) {
    		URL[] locatedUrls = next.locate();
    		if (locatedUrls != null) {
    			for (int i = 0; i < locatedUrls.length; i++) {
    				urls.add(locatedUrls[i]);
    			}
    		}
    		
    	}
        return new ClasspathResolver(filter, urls.toArray(new URL[urls.size()]));
    }

    public static final ClasspathResolver getResolver(ServletContext servletContext) {
        return new ClasspathResolver(new WebappClasspathUrlLocator(servletContext).locate());
    }

    public static final ClasspathResolver getResolver(UrlFilter filter, ServletContext servletContext) {
        return new ClasspathResolver(new WebappClasspathUrlLocator(servletContext).locate());
    }
    
    public URL[] getClasspaths() {
    	return classpaths;
    }

    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append(this.getClasspaths());
    	
    	return builder.toString();
    }
}