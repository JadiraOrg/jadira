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
package org.jadira.scanner.core.helper.filenamefilter;

import java.io.FilenameFilter;

public class ClassFilenameFilter implements FilenameFilter {

	/*
	 *  Packages to potentially ignore for performance... "com.sun", "java", "javassist", "javax", "sun" };
	 */
    protected static final String[] IGNORED_PACKAGES = { }; 
	
	public boolean accept(java.io.File dir, String filename) {

		if (filename.startsWith("/")) {
			filename = filename.substring(1);
		}
		if (filename.endsWith(".class") && !ignorePackage(filename.replace('/', '.'))) {
			// Skip inner classes
			return !filename.contains("$");
		}
		return false;
	}

	private boolean ignorePackage(String packageName) {

		for (String ignored : IGNORED_PACKAGES) {
			if (packageName.startsWith(ignored + ".")) {
				return true;
			}
		}
		return false;
	}
}