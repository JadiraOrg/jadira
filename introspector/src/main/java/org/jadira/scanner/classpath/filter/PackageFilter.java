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

import java.util.HashSet;
import java.util.Set;

import org.jadira.scanner.classpath.api.JElementFilter;
import org.jadira.scanner.classpath.types.JElement;
import org.jadira.scanner.classpath.types.JPackage;
import org.jadira.scanner.core.filter.AbstractFilter;

public class PackageFilter extends AbstractFilter<JElement> implements JElementFilter {

	private Set<String> packageNames = new HashSet<String>();
	
	public PackageFilter() {
	}
	
	public PackageFilter(Package... packages) {
		for (Package next : packages) {
			packageNames.add(next.getName());
		}
	}
	
	public PackageFilter(JPackage... packages) {
		for (JPackage next : packages) {
			packageNames.add(next.getName());
		}
	}
	
	public PackageFilter(String... packageNames) {
		for (String next : packageNames) {
			this.packageNames.add(next);
		}
	}
	
	@Override
	public boolean accept(JElement element) {
		
		final boolean shouldAccept;
		if (JPackage.class.isAssignableFrom(element.getClass())) {
			if (packageNames.isEmpty()) {
				shouldAccept = true;
			} else {
				if (packageNames.contains(((JPackage)element).getName())) {
					shouldAccept = true;
				} else {
					shouldAccept = false;
				}
			}
		} else {
			shouldAccept = false;
		}
		return shouldAccept;
	}
}
