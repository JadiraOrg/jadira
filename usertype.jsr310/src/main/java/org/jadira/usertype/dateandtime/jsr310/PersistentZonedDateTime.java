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
package org.jadira.usertype.dateandtime.jsr310;

import java.sql.Timestamp;
import java.util.Properties;

import javax.time.calendar.TimeZone;
import javax.time.calendar.ZonedDateTime;

import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.jsr310.columnmapper.TimestampColumnZonedDateTimeMapper;
import org.jadira.usertype.dateandtime.shared.spi.AbstractVersionableUserType;
import org.jadira.usertype.dateandtime.shared.spi.ConfigurationHelper;

/**
 * Persist {@link ZonedDateTime} via Hibernate. This type is
 * mostly compatible with {@link org.joda.time.contrib.hibernate.PersistentDateTime} however
 * you should note that JodaTime's {@link org.joda.time.DateTime} has only millisecond precision,
 * whilst JSR 310 offers nanosecond precision. When interpreting nanosecond values, Joda time will
 * round down to the nearest millisecond. The type is stored using UTC timezone and presented in the
 * JVM using the JVM's default zone.
 *
 * Alternatively provide the 'databaseZone' parameter in the {@link TimeZone#forID(String)} format
 * to indicate the zone of the database. The 'javaZone' can be used to similarly configure the zone of the
 * value on return from the database.
 * N.B. To use the zone of the JVM supply 'jvm'
 */
public class PersistentZonedDateTime extends AbstractVersionableUserType<ZonedDateTime, Timestamp, TimestampColumnZonedDateTimeMapper> implements ParameterizedType {

    private static final long serialVersionUID = -917119312070336022L;

    @Override
    public void setParameterValues(Properties parameters) {

        super.setParameterValues(parameters);

        if (parameters != null) {

            TimestampColumnZonedDateTimeMapper columnMapper = getColumnMapper();

            String databaseZone = parameters.getProperty("databaseZone");
    		if (databaseZone == null) {
    			databaseZone = ConfigurationHelper.getProperty("databaseZone");
    		}
    		
            if (databaseZone != null) {
                if ("jvm".equals(databaseZone)) {
                    columnMapper.setDatabaseZone(null);
                } else {
                    columnMapper.setDatabaseZone(TimeZone.of(databaseZone));
                }
            }

            String javaZone = parameters.getProperty("javaZone");
    		if (javaZone == null) {
    			javaZone = ConfigurationHelper.getProperty("javaZone");
    		}
    		
            if (javaZone != null) {
                if ("jvm".equals(javaZone)) {
                    columnMapper.setJavaZone(null);
                } else {
                    columnMapper.setJavaZone(TimeZone.of(javaZone));
                }
            }
        }
    }

    @Override
    public int compare(Object o1, Object o2) {
        return ((ZonedDateTime) o1).compareTo((ZonedDateTime) o2);
    }
}
