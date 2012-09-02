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

import java.util.Locale;

import org.jadira.bindings.core.api.Binding;

/**
 * Binds a Locale to a String
 */
public class LocaleStringBinding extends AbstractStringBinding<Locale> implements Binding<Locale, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale unmarshal(String object) {
        
        String[] components = object.split("_", 3);
        
        final Locale result;
        
        if (components.length == 1) {
            result = new Locale(components[0]);
        } else if (components.length == 2) {
            result = new Locale(components[0], components[1]);
        } else if (components.length == 3) {
            result = new Locale(components[0], components[1], components[2]);
        } else {
            throw new IllegalArgumentException("Unable to unmarshall String to Locale: " + object);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public Class<Locale> getBoundClass() {
		return Locale.class;
	}
}
