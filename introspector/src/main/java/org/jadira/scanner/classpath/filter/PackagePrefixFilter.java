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
package org.jadira.scanner.classpath.filter;

import org.jadira.scanner.classpath.api.JElementFilter;
import org.jadira.scanner.classpath.types.JElement;
import org.jadira.scanner.classpath.types.JPackage;
import org.jadira.scanner.core.filter.AbstractFilter;

public class PackagePrefixFilter extends AbstractFilter<JElement> implements JElementFilter {
	
	private String prefix;

	public PackagePrefixFilter(String prefix) {
		this.prefix = prefix;
	}
	
	public PackagePrefixFilter(Package prefix) {
		this.prefix = prefix.getName();
	}
	
	public PackagePrefixFilter(JPackage prefix) {
		this.prefix = prefix.getName();
	}
	
	@Override
	public boolean accept(JElement element) {
		return element.getName().substring(0, element.getName().lastIndexOf('.')).equals(prefix);
	}
	
	public String getPrefix() {
		return prefix;
	}
}
