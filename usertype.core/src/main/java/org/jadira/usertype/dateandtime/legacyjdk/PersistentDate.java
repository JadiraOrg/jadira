/*
 *  Copyright 2010, 2011, 2012 Christopher Pheby
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
package org.jadira.usertype.dateandtime.legacyjdk;

import java.sql.Timestamp;

import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.legacyjdk.columnmapper.TimestampColumnDateMapper;
import org.jadira.usertype.spi.shared.AbstractVersionableUserType;
import org.jadira.usertype.spi.shared.IntegratorConfiguredType;

/**
 * Persist {@link java.util.Date} via Hibernate. The type is stored using the timezone as configured 
 * using Hibernate's configuration property 'hibernate.jdbc.time_zone' and presented in the
 * JVM using the JVM's default zone.
 *
 * Alternatively provide the 'javaZone' can be used to similarly configure the zone of the
 * value on return from the database.
 */
public class PersistentDate extends AbstractVersionableUserType<java.util.Date, Timestamp, TimestampColumnDateMapper> implements ParameterizedType, IntegratorConfiguredType {

    private static final long serialVersionUID = -6656619988954550389L;

    @Override
    public int compare(Object o1, Object o2) {
        return ((java.util.Date) o1).compareTo((java.util.Date) o2);
    }
}
