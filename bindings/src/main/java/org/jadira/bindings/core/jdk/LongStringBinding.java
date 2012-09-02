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

/**
 * Binds a Long to a String
 */
public class LongStringBinding extends AbstractStringBinding<Long> implements Binding<Long, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Long unmarshal(String object) {
        return Long.valueOf(Long.parseLong(object));
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public Class<Long> getBoundClass() {
		return Long.class;
	}
}
