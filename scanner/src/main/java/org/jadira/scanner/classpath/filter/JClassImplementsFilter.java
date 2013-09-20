/*
 *  Copyright 2013 Chris Pheby
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jadira.scanner.classpath.types.JClass;
import org.jadira.scanner.classpath.types.JInterface;
import org.jadira.scanner.core.filter.AbstractFilter;

public class JClassImplementsFilter extends AbstractFilter<JClass> {

	private Set<Class<?>> types = new HashSet<Class<?>>();
	
	public JClassImplementsFilter() {
	}
	
	public JClassImplementsFilter(Class<?> type) {
		if (type != null) {
			types.add(type);
		}
	}
	
	public JClassImplementsFilter(Collection<Class<?>> types) {
		for (Class<?> next : types) {
			types.add(next);
		}
	}
	
	@Override
	public boolean accept(JClass clazz) {
		
	    while (true) {
	        JClass superType = clazz.getSuperType();
	        if (superType == null) {
	            return false;
	        }
	        for (JInterface iface : superType.getImplementedInterfaces()) {
    	        if (types.contains(iface.getActualClass())) {
    	            return true;
    	        } 
	        }
	    }
	}
}
