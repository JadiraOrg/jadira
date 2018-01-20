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
import java.time.OffsetTime;

import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.threeten.columnmapper.TimestampColumnOffsetTimeMapper;
import org.jadira.usertype.spi.shared.AbstractParameterizedUserType;
import org.jadira.usertype.spi.shared.IntegratorConfiguredType;

/**
 * Persist {@link OffsetTime} via Hibernate using nanoseconds of the day. This uses a long value stored as nanoseconds
 * in the database.
 * The type is stored using the timezone as configured 
 * using Hibernate's configuration property 'hibernate.jdbc.time_zone' and presented in the
 * JVM using the JVM's default zone. You can optionally override or use as an alternative to this property the 
 * parameter 'databaseZone' on this type.
 *
 * Alternatively provide the 'javaZone' can be used to similarly configure the zone of the
 * value on return from the database.
 */
public class PersistentOffsetTimeAsTimestamp extends AbstractParameterizedUserType<OffsetTime, Timestamp, TimestampColumnOffsetTimeMapper> implements ParameterizedType, IntegratorConfiguredType {

    private static final long serialVersionUID = 2629423108971922341L;
}
