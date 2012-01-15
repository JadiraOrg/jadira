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
package org.jadira.usertype.dateandtime.joda.columnmapper;

import java.sql.Time;

import org.jadira.usertype.spi.shared.AbstractTimeColumnMapper;
import org.joda.time.TimeOfDay;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * @deprecated Recommend replacing use of {@link TimeOfDay} with {@link org.joda.time.LocalTime} and {@link org.jadira.usertype.dateandtime.joda.PersistentLocalTime}
 */
public class TimeColumnTimeOfDayMapper extends AbstractTimeColumnMapper<TimeOfDay> {

    private static final long serialVersionUID = 6734385103313158326L;

    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter();

    @Override
    public TimeOfDay fromNonNullString(String s) {
        return new TimeOfDay(s);
    }

    @Override
    public TimeOfDay fromNonNullValue(Time value) {
        return LOCAL_TIME_FORMATTER.parseDateTime(value.toString()).toTimeOfDay();
    }

    @Override
    public String toNonNullString(TimeOfDay value) {
        return value.toString();
    }

    @Override
    public Time toNonNullValue(TimeOfDay value) {
        return Time.valueOf(LOCAL_TIME_FORMATTER.print((TimeOfDay) value));
    }
}
