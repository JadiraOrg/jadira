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
package org.jadira.scanner.introspection.types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import javassist.bytecode.MethodInfo;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspDefaultConstructor extends InspConstructor {

    private static final List<InspParameter> EMPTY_PARAMS = Collections.emptyList();

	protected InspDefaultConstructor(MethodInfo methodInfo, InspClass inspClass, ClasspathResolver resolver) {
        super(methodInfo, inspClass, resolver);
    }

    public static InspDefaultConstructor getInspDefaultConstructor(MethodInfo methodInfo, InspClass inspClass, ClasspathResolver resolver) {
        return new InspDefaultConstructor(methodInfo, inspClass, resolver);
    }

    @Override
    public Constructor<?> getActualConstructor() throws ClasspathAccessException {

        try {
            Class<?> clazz = ((InspClass) getEnclosingType()).getActualClass();
            return clazz.getConstructor();
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Could not access constructor: " + e, e);
        } catch (NoSuchMethodException e) {
            throw new ClasspathAccessException("Could not find constructor: " + e, e);
        }
    }
    
    public List<InspParameter> getParameters() throws ClasspathAccessException {

        return EMPTY_PARAMS;
    }

    public Method getActualMethod() throws ClasspathAccessException {

        try {
            return getEnclosingType().getActualClass().getMethod(getName());
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Problem obtaining method: " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new ClasspathAccessException("Problem finding method: " + e.getMessage(), e);
        }
    }

}