/*
;po;. *  Copyright 2010 Christopher Pheby
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
package org.jadira.usertype.dateandtime.jsr310.columnmapper;

import static org.jadira.usertype.dateandtime.jsr310.utils.ZoneHelper.getDefaultTimeZone;

import java.sql.Timestamp;

import javax.time.calendar.LocalDateTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZonedDateTime;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatterBuilder;

import org.jadira.usertype.dateandtime.shared.spi.AbstractVersionableTimestampColumnMapper;

/**
 * Maps a precise datetime column for storage. The UTC Zone will be used to store the value
 */
public class TimestampColumnZonedDateTimeMapper extends AbstractVersionableTimestampColumnMapper<ZonedDateTime> {

    private static final long serialVersionUID = -7670411089210984705L;

    public static final DateTimeFormatter DATETIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ssffn").toFormatter();

    private TimeZone databaseZone = TimeZone.UTC;
    
    private TimeZone javaZone = null;

    @Override
    public ZonedDateTime fromNonNullString(String s) {
        return ZonedDateTime.parse(s);
    }

    @Override
    public ZonedDateTime fromNonNullValue(Timestamp value) {
        
        TimeZone currentDatabaseZone = databaseZone == null ? getDefaultTimeZone() : databaseZone;
        TimeZone currentJavaZone = javaZone == null ? getDefaultTimeZone() : javaZone;

        LocalDateTime localDateTime = DATETIME_FORMATTER.parse(value.toString()).merge().get(LocalDateTime.rule());
        ZonedDateTime dateTime = localDateTime.atZone(currentDatabaseZone);
        return dateTime.withZoneSameInstant(currentJavaZone);
    }

    @Override
    public String toNonNullString(ZonedDateTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(ZonedDateTime value) {

        TimeZone currentDatabaseZone = databaseZone == null ? getDefaultTimeZone() : databaseZone;
        
        value = value.withZoneSameInstant(currentDatabaseZone);
        
        String formattedTimestamp = DATETIME_FORMATTER.print(value);
        if (formattedTimestamp.endsWith(".")) {
            formattedTimestamp = formattedTimestamp.substring(0, formattedTimestamp.length() - 1);
        }

        final Timestamp timestamp = Timestamp.valueOf(formattedTimestamp);
        return timestamp;
    }
    
    public void setDatabaseZone(TimeZone databaseZone) {
        this.databaseZone = databaseZone;
    }

    public void setJavaZone(TimeZone javaZone) {
        this.javaZone = javaZone;
    }
}
