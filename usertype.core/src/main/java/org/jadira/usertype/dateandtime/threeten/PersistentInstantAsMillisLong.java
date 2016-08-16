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
package org.jadira.usertype.dateandtime.threeten;

import java.time.Instant;

import org.jadira.usertype.dateandtime.threeten.columnmapper.LongColumnInstantMapper;
import org.jadira.usertype.spi.shared.AbstractVersionableUserType;

/**
 * Persist {@link Instant} via Hibernate using milliseconds of the day. This type is
 * mostly compatible with org.joda.time.contrib.hibernate.PersistentInstantExact however
 * you should note that JodaTime's org.joda.time.Instant has only millisecond precision,
 * whilst JSR 310 offers nanosecond precision. When interpreting nanosecond values, Joda time will
 * round down to the nearest millisecond.
 * @see PersistentInstantAsNanosBigInteger
 */
public class PersistentInstantAsMillisLong extends AbstractVersionableUserType<Instant, Long, LongColumnInstantMapper> {

    private static final long serialVersionUID = 3793471253953101728L;

    @Override
    public int compare(Object o1, Object o2) {
        return ((Instant) o1).compareTo((Instant) o2);
    }
}
