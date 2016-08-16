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

import java.sql.Timestamp;
import java.time.Instant;

import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.threeten.columnmapper.TimestampColumnInstantMapper;
import org.jadira.usertype.spi.shared.AbstractVersionableUserType;
import org.jadira.usertype.spi.shared.IntegratorConfiguredType;

/**
 * Persist {@link Instant} via Hibernate using a JDBC Timestamp datatype with a reference date.  - note that sub-second values will not
 * be retained. The type is stored using UTC timezone.
 *
 * Alternatively provide the 'databaseZone' parameter in the {@link java.util.TimeZone#getTimeZone(String)} format
 * to indicate the zone of the database.
 * N.B. To use the zone of the JVM supply 'jvm'
 */
public class PersistentInstantAsTimestamp extends AbstractVersionableUserType<Instant, Timestamp, TimestampColumnInstantMapper> implements ParameterizedType, IntegratorConfiguredType {

    private static final long serialVersionUID = -1184009888235202420L;

    @Override
    public int compare(Object o1, Object o2) {
        return ((Instant) o1).compareTo((Instant) o2);
    }
}
