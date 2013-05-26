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
package org.jadira.usertype.dateandtime.joda;

import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.joda.columnmapper.TimestampColumnDateTimeMapper;
import org.jadira.usertype.spi.shared.AbstractParameterizedMultiColumnUserType;
import org.jadira.usertype.spi.shared.IntegratorConfiguredType;
import org.jadira.usertype.spi.utils.reflection.ArrayUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Persist {@link Interval} via Hibernate. The interval type is intended to be compatible with
 * {@link org.joda.time.contrib.hibernate.PersistentInterval} and stores beginning and end values
 * using the UTC zone and presented in the
 * JVM using the JVM's default zone.
 *
 * Alternatively provide the 'databaseZone' parameter in the {@link org.joda.time.DateTimeZone#forID(String)} format
 * to indicate the zone of the database. The 'javaZone' can be used to similarly configure the zone of the
 * value on return from the database.
 * N.B. To use the zone of the JVM supply 'jvm'
 */
public class PersistentInterval extends AbstractParameterizedMultiColumnUserType<Interval> implements ParameterizedType, IntegratorConfiguredType {

    private static final long serialVersionUID = 1364221029392346011L;

    private static final TimestampColumnDateTimeMapper[] COLUMN_MAPPERS = new TimestampColumnDateTimeMapper[] { new TimestampColumnDateTimeMapper(), new TimestampColumnDateTimeMapper() };

    private static final String[] PROPERTY_NAMES = new String[]{ "begin", "end" };
    
    @Override
    protected Interval fromConvertedColumns(Object[] convertedColumns) {

        DateTime begin = (DateTime) convertedColumns[0];
        DateTime end = (DateTime) convertedColumns[1];

        return new Interval(begin, end);
    }

    @Override
    protected TimestampColumnDateTimeMapper[] getColumnMappers() {
        return COLUMN_MAPPERS;
    }

    @Override
    protected Object[] toConvertedColumns(Interval value) {

        return new Object[] { value.getStart(), value.getEnd() };
    }

    @Override
    public String[] getPropertyNames() {

        return ArrayUtils.copyOf(PROPERTY_NAMES);
    }
}
