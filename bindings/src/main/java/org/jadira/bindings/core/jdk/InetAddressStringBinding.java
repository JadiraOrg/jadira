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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jadira.bindings.core.api.Binding;

/**
 * Binds a InetAddress to a String
 */
public class InetAddressStringBinding extends AbstractStringBinding<InetAddress> implements Binding<InetAddress, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress unmarshal(String object) {

        try {
            return InetAddress.getByName(object);
        } catch (UnknownHostException ex) {
            throw new IllegalArgumentException("Unknown host " + object + ":" + object);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String marshal(InetAddress object) {
        return object.getHostAddress();
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public Class<InetAddress> getBoundClass() {
		return InetAddress.class;
	}
}
