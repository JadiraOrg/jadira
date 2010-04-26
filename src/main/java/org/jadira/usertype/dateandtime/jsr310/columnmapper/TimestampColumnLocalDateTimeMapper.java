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
package org.jadira.usertype.dateandtime.jsr310.columnmapper;

import java.sql.Timestamp;

import javax.time.calendar.LocalDateTime;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatterBuilder;

import org.jadira.usertype.dateandtime.shared.spi.AbstractTimestampColumnMapper;


public class TimestampColumnLocalDateTimeMapper extends AbstractTimestampColumnMapper<LocalDateTime> {

    private static final long serialVersionUID = -7670411089210984705L;

    public static final DateTimeFormatter LOCAL_DATETIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ssffn").toFormatter();

    @Override
    public LocalDateTime fromNonNullString(String s) {
        return LocalDateTime.parse(s);
    }

    @Override
    public LocalDateTime fromNonNullValue(Timestamp value) {
        return LOCAL_DATETIME_FORMATTER.parse(value.toString()).merge().get(LocalDateTime.rule());
    }

    @Override
    public String toNonNullString(LocalDateTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(LocalDateTime value) {

        final String formattedTimestamp = LOCAL_DATETIME_FORMATTER.print((LocalDateTime) value);
        final Timestamp timestamp = Timestamp.valueOf(formattedTimestamp);

        return timestamp;
    }
}
