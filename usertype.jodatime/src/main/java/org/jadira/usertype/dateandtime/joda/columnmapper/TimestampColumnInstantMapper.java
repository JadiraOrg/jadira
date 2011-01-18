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
package org.jadira.usertype.dateandtime.joda.columnmapper;

import java.sql.Timestamp;

import org.jadira.usertype.dateandtime.shared.spi.AbstractVersionableTimestampColumnMapper;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class TimestampColumnInstantMapper extends AbstractVersionableTimestampColumnMapper<Instant> {

    private static final long serialVersionUID = -7670411089210984705L;

    public static final DateTimeFormatter LOCAL_DATETIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss'.'").appendFractionOfSecond(0, 9).toFormatter();

    private DateTimeZone databaseZone = DateTimeZone.UTC;
    
    @Override
    public Instant fromNonNullString(String s) {
        return new Instant(s);
    }

    @Override
    public Instant fromNonNullValue(Timestamp value) {
        
        DateTimeZone currentDatabaseZone = databaseZone == null ? getDefault() : databaseZone;
        
        return LOCAL_DATETIME_FORMATTER.withZone(currentDatabaseZone).parseDateTime(value.toString()).toInstant();
    }

    @Override
    public String toNonNullString(Instant value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(Instant value) {

        DateTimeZone currentDatabaseZone = databaseZone == null ? getDefault() : databaseZone;
        
        String formattedTimestamp = LOCAL_DATETIME_FORMATTER.withZone(currentDatabaseZone).print(value);
        if (formattedTimestamp.endsWith(".")) {
            formattedTimestamp = formattedTimestamp.substring(0, formattedTimestamp.length() - 1);
        }
        
        final Timestamp timestamp = Timestamp.valueOf(formattedTimestamp);
        return timestamp;
    }

    public void setDatabaseZone(DateTimeZone databaseZone) {
        this.databaseZone = databaseZone;
    }
    
    private static DateTimeZone getDefault() {

        DateTimeZone zone = null;
        try {
            try {
                String id = System.getProperty("user.timezone");
                if (id != null) {
                    zone = DateTimeZone.forID(id);
                }
            } catch (RuntimeException ex) { }
            if (zone == null) {
                zone = DateTimeZone.forID(java.util.TimeZone.getDefault().getID());
            }
        } catch (RuntimeException ex) { }
        if (zone == null) {
            zone = DateTimeZone.UTC;
        }
        return zone;
    }
}
