/*
;po;. *  Copyright 2010, 2011 Christopher Pheby
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
package org.jadira.usertype.dateandtime.threetenbp.columnmapper;

import static org.jadira.usertype.dateandtime.threetenbp.utils.ZoneHelper.getDefaultZoneOffset;

import java.sql.Timestamp;

import org.jadira.usertype.spi.shared.AbstractVersionableTimestampColumnMapper;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;

/**
 * Maps a precise datetime column for storage. The UTC Zone will be used to store the value
 */
public class TimestampColumnZonedDateTimeMapper extends AbstractVersionableTimestampColumnMapper<ZonedDateTime> {

    private static final long serialVersionUID = -7670411089210984705L;

    public static final DateTimeFormatter DATETIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ssffn").toFormatter();

    private ZoneOffset databaseZone = ZoneOffset.UTC;

    private ZoneOffset javaZone = null;

    @Override
    public ZonedDateTime fromNonNullString(String s) {
        return ZonedDateTime.parse(s);
    }

    @Override
    public ZonedDateTime fromNonNullValue(Timestamp value) {

    	ZoneOffset currentDatabaseZone = databaseZone == null ? getDefaultZoneOffset() : databaseZone;
    	ZoneOffset currentJavaZone = javaZone == null ? getDefaultZoneOffset() : javaZone;

        LocalDateTime localDateTime = LocalDateTime.parse(value.toString(), DATETIME_FORMATTER);
        ZonedDateTime dateTime = localDateTime.atZone(currentDatabaseZone);
        return dateTime.withZoneSameInstant(currentJavaZone);
    }

    @Override
    public String toNonNullString(ZonedDateTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(ZonedDateTime value) {

    	ZoneOffset currentDatabaseZone = databaseZone == null ? getDefaultZoneOffset() : databaseZone;

        value = value.withZoneSameInstant(currentDatabaseZone);

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
