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
package org.jadira.usertype.dateandtime.jsr310.columnmapper;

import static org.jadira.usertype.dateandtime.jsr310.utils.ZoneHelper.getDefaultZoneOffset;

import java.sql.Timestamp;

import javax.time.calendar.LocalDateTime;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatterBuilder;

import org.jadira.usertype.spi.shared.AbstractVersionableTimestampColumnMapper;

/**
 * Maps a precise datetime column for storage. The UTC Zone will be used to store the value
 */
public class TimestampColumnOffsetDateTimeMapper extends AbstractVersionableTimestampColumnMapper<OffsetDateTime> {

    private static final long serialVersionUID = -7670411089210984705L;

    public static final DateTimeFormatter DATETIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ssffn").toFormatter();

    private ZoneOffset databaseZone = ZoneOffset.UTC;

    private ZoneOffset javaZone = null;

    @Override
    public OffsetDateTime fromNonNullString(String s) {
        return OffsetDateTime.parse(s);
    }

    @Override
    public OffsetDateTime fromNonNullValue(Timestamp value) {

        ZoneOffset currentDatabaseZone = databaseZone == null ? getDefaultZoneOffset() : databaseZone;
        ZoneOffset currentJavaZone = javaZone == null ? getDefaultZoneOffset() : javaZone;

        LocalDateTime localDateTime = DATETIME_FORMATTER.parse(value.toString()).merge().get(LocalDateTime.rule());
        OffsetDateTime dateTime = localDateTime.atOffset(currentDatabaseZone);
        return dateTime.withOffsetSameInstant(currentJavaZone);
    }

    @Override
    public String toNonNullString(OffsetDateTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(OffsetDateTime value) {

        ZoneOffset currentDatabaseZone = databaseZone == null ? getDefaultZoneOffset() : databaseZone;

        value = value.withOffsetSameInstant(currentDatabaseZone);

        String formattedTimestamp = DATETIME_FORMATTER.print(value);
        if (formattedTimestamp.endsWith(".")) {
            formattedTimestamp = formattedTimestamp.substring(0, formattedTimestamp.length() - 1);
        }

        final Timestamp timestamp = Timestamp.valueOf(formattedTimestamp);
        return timestamp;
    }

    public void setDatabaseZone(ZoneOffset databaseZone) {
        this.databaseZone = databaseZone;
    }

    public void setJavaZone(ZoneOffset javaZone) {
        this.javaZone = javaZone;
    }
}
