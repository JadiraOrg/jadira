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

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class JvmTimestampSeed implements Seed<Timestamp>, Serializable {

    private static final long serialVersionUID = -5960197971311717694L;

    @Override
    public Timestamp getTimestamp(SharedSessionContractImplementor session) {
        return new Timestamp(System.currentTimeMillis());
    }

    @Override
    public Timestamp getNextTimestamp(Timestamp current, SharedSessionContractImplementor session) {
        return new Timestamp(System.currentTimeMillis());
    }
}
