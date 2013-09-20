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

import org.jadira.scanner.classpath.types.JAnnotation;
import org.jadira.scanner.classpath.types.JClass;
import org.jadira.scanner.classpath.types.JInterface;
import org.jadira.scanner.classpath.types.JType;
import org.jadira.scanner.core.filter.AbstractFilter;

public class JTypeSubTypeOfFilter extends AbstractFilter<JType> {

	private Set<Class<?>> types = new HashSet<Class<?>>();
	
	public JTypeSubTypeOfFilter() {
	}
	
	public JTypeSubTypeOfFilter(Class<?> type) {
		if (type != null) {
			types.add(type);
		}
	}
	
	public JTypeSubTypeOfFilter(Collection<Class<?>> types) {
		for (Class<?> next : types) {
			types.add(next);
		}
	}
	
	@Override
	public boolean accept(JType type) {

        if (type instanceof JAnnotation) {
            JType superType = ((JAnnotation<?>)type).getSuperType();
            if (superType == null) {
                return false;
            }
            if (types.contains(superType.getActualClass())) {
                return true;
            }
        } 
        
        if (type instanceof JClass) {
            JType superType = ((JClass)type).getSuperType();
            if (superType == null) {
                return false;
            }
            if (types.contains(superType.getActualClass())) {
                return true;
            }
        }
        
        if (type instanceof JInterface) {
            for (JType superType : ((JInterface) type).getSuperInterfaces()) {
                if (superType != null && types.contains(superType.getActualClass())) {
                    return true;
                }
            }
            return false;
        }
        
        throw new IllegalStateException("Unexpected JType with declared type of: " + type.getClass().getName());
	}
}
