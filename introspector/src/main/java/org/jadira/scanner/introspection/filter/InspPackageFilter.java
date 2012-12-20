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
package org.jadira.scanner.introspection.filter;

import java.util.HashSet;
import java.util.Set;

import org.jadira.scanner.api.InspElementFilter;
import org.jadira.scanner.introspection.types.InspElement;
import org.jadira.scanner.introspection.types.InspPackage;

public class InspPackageFilter implements InspElementFilter {

	private Set<String> packageNames = new HashSet<String>();
	
	public InspPackageFilter() {
	}
	
	public InspPackageFilter(Package... packages) {
		for (Package next : packages) {
			packageNames.add(next.getName());
		}
	}
	
	public InspPackageFilter(InspPackage... packages) {
		for (InspPackage next : packages) {
			packageNames.add(next.getName());
		}
	}
	
	public InspPackageFilter(String... packageNames) {
		for (String next : packageNames) {
			this.packageNames.add(next);
		}
	}
	
	@Override
	public boolean accept(InspElement element) {
		
		final boolean shouldAccept;
		if (InspPackage.class.isAssignableFrom(element.getClass())) {
			if (packageNames.isEmpty()) {
				shouldAccept = true;
			} else {
				if (packageNames.contains(((InspPackage)element).getName())) {
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
