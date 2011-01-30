/*
 *  Copyright 2010 Christopher Pheby
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
package org.jadira.usertype.dateandtime.shared.spi;

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.DbTimestampType;

public class DbTimestampSeed extends JvmTimestampSeed {

    private static final long serialVersionUID = 5223082239565556715L;

    private final DbTimestampType helper = new DbTimestampType();
    
    public Timestamp getTimestamp(SessionImplementor session) {
        Object result = helper.seed(session);
        if (result instanceof Timestamp) {
            return (Timestamp) result;
        } else {
            return new Timestamp(((Date) result).getTime());
        }
    }

    public Timestamp getNextTimestamp(Timestamp current, SessionImplementor session) {
        Object result = helper.next(current, session);
        if (result instanceof Timestamp) {
            return (Timestamp) result;
        } else {
            return new Timestamp(((Date) result).getTime());
        }
    }
}
