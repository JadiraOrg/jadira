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
package org.jadira.usertype.dateandtime.jsr310;

import java.sql.Date;
import java.util.Properties;

import javax.time.calendar.OffsetDate;
import javax.time.calendar.ZoneOffset;

import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.jsr310.columnmapper.DateColumnOffsetDateMapper;
import org.jadira.usertype.dateandtime.shared.spi.AbstractUserType;

/**
 * Persist {@link OffsetDate} via Hibernate. The offset will be stored in an extra column.
 * When interpreting nanosecond values, Joda time will
 * round down to the nearest millisecond. The type is stored using UTC timezone and presented in the 
 * JVM using the JVM's default zone.
 * 
 * Alternatively provide the 'databaseZone' parameter in the {@link javax.time.calendar.TimeZone#forID(String)} format
 * to indicate the zone of the database. The 'javaZone' can be used to similarly configure the zone of the
 * value on return from the database.
 * N.B. To use the zone of the JVM supply 'jvm'
 */
public class PersistentOffsetDate extends AbstractUserType<OffsetDate, Date, DateColumnOffsetDateMapper> implements ParameterizedType {
    
    public void setParameterValues(Properties parameters) {

        if (parameters != null) {
            
            DateColumnOffsetDateMapper columnMapper = (DateColumnOffsetDateMapper) getColumnMapper();
            
            String databaseZone = parameters.getProperty("databaseZone");
            if (databaseZone != null) {
                if ("jvm".equals(databaseZone)) {
                    columnMapper.setDatabaseZone(null);
                } else {
                    columnMapper.setDatabaseZone(ZoneOffset.of(databaseZone));
                }
            }
            String javaZone = parameters.getProperty("javaZone");
            if (javaZone != null) {
                if ("jvm".equals(javaZone)) {
                    columnMapper.setJavaZone(null);
                } else {
                    columnMapper.setJavaZone(ZoneOffset.of(javaZone));
                }
            }
        }
    }
}
