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

import java.sql.Timestamp;

import javax.time.calendar.LocalTime;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatterBuilder;

import org.jadira.usertype.spi.shared.AbstractTimestampColumnMapper;


public class TimestampColumnLocalTimeMapper extends AbstractTimestampColumnMapper<LocalTime> {

    private static final long serialVersionUID = 1921591625617366103L;

    public static final DateTimeFormatter LOCAL_DATETIME_PRINTER = new DateTimeFormatterBuilder().appendPattern("0001-01-01 HH:mm:ssffn").toFormatter();
    public static final DateTimeFormatter LOCAL_DATETIME_PARSER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ssffn").toFormatter();

    @Override
    public LocalTime fromNonNullString(String s) {
        return LocalTime.parse(s);
    }

    @Override
    public LocalTime fromNonNullValue(Timestamp value) {
        return LOCAL_DATETIME_PARSER.parse(value.toString()).merge().get(LocalTime.rule());
    }

    @Override
    public String toNonNullString(LocalTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(LocalTime value) {

        final String formattedTimestamp = LOCAL_DATETIME_PRINTER.print((LocalTime) value);
        final Timestamp timestamp = Timestamp.valueOf(formattedTimestamp);

        return timestamp;
    }

}
