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
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class TimeColumnLocalTimeMapper extends AbstractTimeColumnMapper<LocalTime> {

    private static final long serialVersionUID = 6734385103313158326L;

    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter();

    public TimeColumnLocalTimeMapper() {
    }

    @Override
    public LocalTime fromNonNullString(String s) {
        return new LocalTime(s);
    }

    @Override
    public LocalTime fromNonNullValue(Time value) {

        DateTime dateTime = new DateTime(value.getTime());
        LocalTime localTime = dateTime.toLocalTime();

        return localTime;
    }

    @Override
    public String toNonNullString(LocalTime value) {
        return value.toString();
    }

    @Override
    public Time toNonNullValue(LocalTime value) {

    	DateTime zonedValue = new LocalDateTime(
    			1970,1,1,value.getHourOfDay(), value.getMinuteOfHour(), value.getSecondOfMinute(), value.getMillisOfSecond(), value.getChronology()
    	).toDateTime();

        final Time time = new Time(zonedValue.getMillis());
        return time;
    }
}
