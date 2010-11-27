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

import java.sql.Timestamp;
import java.util.Properties;

import javax.time.Instant;
import javax.time.calendar.TimeZone;

import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.jsr310.columnmapper.TimestampColumnInstantMapper;
import org.jadira.usertype.dateandtime.shared.spi.AbstractUserType;
import org.joda.time.DateTimeZone;

/**
 * Persist {@link Instant} via Hibernate using a JDBC Timestamp datatype with a reference date.  - note that sub-second values will not
 * be retained. The type is stored using UTC timezone.
 * 
 * Alternatively provide the 'databaseZone' parameter in the {@link DateTimeZone#forID(String)} format
 * to indicate the zone of the database.
 */
public class PersistentInstantAsTimestamp extends AbstractUserType<Instant, Timestamp, TimestampColumnInstantMapper> implements ParameterizedType {
    
    public void setParameterValues(Properties parameters) {
        
        TimestampColumnInstantMapper columnMapper = (TimestampColumnInstantMapper)getColumnMapper();
        
        String databaseZone = parameters.getProperty("databaseZone");
        if (databaseZone != null) {
            columnMapper.setDatabaseZone(TimeZone.of(databaseZone));
        }
    }
}
