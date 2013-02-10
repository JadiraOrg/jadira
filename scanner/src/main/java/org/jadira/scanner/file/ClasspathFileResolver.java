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
package org.jadira.scanner.file;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.jadira.scanner.core.api.Allocator;
import org.jadira.scanner.core.spi.AbstractFileResolver;
import org.jadira.scanner.file.locator.JdkBaseClasspathUrlLocator;

public class ClasspathFileResolver extends AbstractFileResolver<File> {

	private static final Allocator<File, File> FILE_ASSIGNER = new Allocator<File, File>() {
		@Override
		public File allocate(File e) {
			return e;
	}};
	
    private static final List<URL> JDK_BASE_CLASSPATH_JARS = new JdkBaseClasspathUrlLocator().locate();

    public ClasspathFileResolver() {    	
        super(JDK_BASE_CLASSPATH_JARS);
    }
    
    public ClasspathFileResolver(List<URL> classpaths) {
    	super(JDK_BASE_CLASSPATH_JARS);
    	getDriverData().addAll(classpaths);
    }

	@Override
	protected Allocator<File, File> getAssigner() {
		return FILE_ASSIGNER;
	}
}
