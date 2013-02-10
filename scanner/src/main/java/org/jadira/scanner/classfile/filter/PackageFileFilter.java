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
package org.jadira.scanner.classfile.filter;

import java.io.File;

import org.jadira.scanner.core.api.Filter;
import org.jadira.scanner.core.filter.AbstractFilter;

/**
 * This filter matches files in a corresponding package
 */
public class PackageFileFilter extends AbstractFilter<File> implements Filter<File> {
	
	private static final char FILE_SEPARATOR = System.getProperty("file.separator").charAt(0);
	
	private final String name;

	public PackageFileFilter(String name, boolean trimTrailingClassName) {
		
		String myName = name.replace('.', FILE_SEPARATOR);
		
		if (trimTrailingClassName) {
			int lastDirIndex = myName.lastIndexOf(FILE_SEPARATOR);
			if (lastDirIndex == -1) {
				this.name = myName;
			} else {
				this.name = myName.substring(0, lastDirIndex);
			}
		} else {
			this.name = myName;
		}
	}
	
	@Override
	public boolean accept(File element) {
		
		String matchName = element.getPath();
		int lastDirIndex = matchName.lastIndexOf(FILE_SEPARATOR);
		if (lastDirIndex != -1) {
			matchName = element.getPath().substring(0, lastDirIndex);
		}
		
		boolean accept = matchName.endsWith(name);
		return accept;
	}
}
