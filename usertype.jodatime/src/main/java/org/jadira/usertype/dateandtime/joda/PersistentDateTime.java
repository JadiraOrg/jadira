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
package org.jadira.usertype.dateandtime.joda;

import java.sql.Timestamp;
import java.util.Properties;

import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.joda.columnmapper.TimestampColumnDateTimeMapper;
import org.jadira.usertype.dateandtime.shared.spi.AbstractUserType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Persist {@link DateTime} via Hibernate. This type is
 * mostly compatible with {@link org.joda.time.contrib.hibernate.PersistentDateTime} however
 * you should note that JodaTime's {@link org.joda.time.DateTime} has only millisecond precision,
 * whilst JSR 310 offers nanosecond precision. When interpreting nanosecond values, Joda time will
 * round down to the nearest millisecond. The type is stored using UTC timezone and presented in the 
 * JVM using the JVM's default zone.
 * 
 * Alternatively provide the 'databaseZone' parameter in the {@link DateTimeZone#forID(String)} format
 * to indicate the zone of the database. The 'javaZone' can be used to similarly configure the zone of the
 * value on return from the database.
 * N.B. To use the zone of the JVM supply 'jvm'
 */
public class PersistentDateTime extends AbstractUserType<DateTime, Timestamp, TimestampColumnDateTimeMapper> implements ParameterizedType {
    
    public void setParameterValues(Properties parameters) {

        if (parameters != null) {
            
            TimestampColumnDateTimeMapper columnMapper = (TimestampColumnDateTimeMapper)getColumnMapper();
            
            String databaseZone = parameters.getProperty("databaseZone");
            if (databaseZone != null) {
                if ("jvm".equals(databaseZone)) {
                    columnMapper.setDatabaseZone(null);
                } else {
                    columnMapper.setDatabaseZone(DateTimeZone.forID(databaseZone));
                }
            }
            String javaZone = parameters.getProperty("javaZone");
            if (javaZone != null) {
                if ("jvm".equals(javaZone)) {
                    columnMapper.setJavaZone(null);
                } else {
                    columnMapper.setJavaZone(DateTimeZone.forID(javaZone));
                }
            }
        }
    }
}
