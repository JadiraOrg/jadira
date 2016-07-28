/*
 *  Copyright 2010, 2011 Christopher Pheby
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
package org.jadira.usertype.spi.shared;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface Seed<T> {

    /**
     * Generate an initial version value.
     * @param session The session from which this request originates.
     * @return an instance of the type
     */
    T getTimestamp(SharedSessionContractImplementor session);

    /**
     * Increment the version value.
     * @param session The session from which this request originates.
     * @param current the current version value
     * @return an instance of the type T
     */
    T getNextTimestamp(T current, SharedSessionContractImplementor session);
}
