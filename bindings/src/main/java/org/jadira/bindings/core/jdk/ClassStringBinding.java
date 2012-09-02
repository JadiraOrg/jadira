/*
 *  Copyright 2010, 2011 Chris Pheby
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
package org.jadira.bindings.core.jdk;

import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.utils.reflection.ClassUtils;

/**
 * Binds a Class to a String
 */
@SuppressWarnings("rawtypes")
public class ClassStringBinding extends AbstractStringBinding<Class> implements Binding<Class, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class unmarshal(String object) {

        return ClassUtils.getClass(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String marshal(Class clazz) {
        return ClassUtils.determineReadableClassName(clazz.getName());
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public Class<Class> getBoundClass() {
		return Class.class;
	}
}
