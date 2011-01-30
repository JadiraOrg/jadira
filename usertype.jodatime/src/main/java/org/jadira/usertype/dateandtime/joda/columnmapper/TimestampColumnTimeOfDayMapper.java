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

import org.jadira.usertype.dateandtime.joda.util.Formatter;
import org.jadira.usertype.dateandtime.shared.spi.AbstractTimestampColumnMapper;
import org.joda.time.LocalTime;
import org.joda.time.TimeOfDay;

/**
 * @deprecated Recommend replacing use of {@link TimeOfDay} with {@link org.joda.time.LocalTime} and {@link org.jadira.usertype.dateandtime.joda.PersistentLocalTimeAsTimestamp}
 */
public class TimestampColumnTimeOfDayMapper extends AbstractTimestampColumnMapper<TimeOfDay> {

    private static final long serialVersionUID = 1921591625617366103L;
    
    @Override
    public TimeOfDay fromNonNullString(String s) {
        return new TimeOfDay(s);
    }

    @Override
    public TimeOfDay fromNonNullValue(Timestamp value) {
        final LocalTime localTime = Formatter.LOCAL_DATETIME_PARSER.parseDateTime(value.toString()).toLocalTime();
        final TimeOfDay timeOfDay = new TimeOfDay(localTime.getHourOfDay(), localTime.getMinuteOfHour(), localTime.getSecondOfMinute(), localTime.getMillisOfSecond(), localTime.getChronology());
        return timeOfDay;
    }

    @Override
    public String toNonNullString(TimeOfDay value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(TimeOfDay value) {

        String formattedTimestamp = Formatter.LOCAL_DATETIME_PRINTER.print(value.toLocalTime());
        if (formattedTimestamp.endsWith(".")) {
            formattedTimestamp = formattedTimestamp.substring(0, formattedTimestamp.length() - 1);
        }
        
        final Timestamp timestamp = Timestamp.valueOf(formattedTimestamp);
        return timestamp;
    }

}
