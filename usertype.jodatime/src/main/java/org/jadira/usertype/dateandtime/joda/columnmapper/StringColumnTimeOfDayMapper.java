/*
 * Copyright 2010 Christopher Pheby Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package org.jadira.usertype.dateandtime.joda.columnmapper;

import org.jadira.usertype.dateandtime.shared.spi.AbstractStringColumnMapper;
import org.joda.time.TimeOfDay;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * @deprecated Recommend replacing use of {@link TimeOfDay} with {@link org.joda.time.LocalTime} and
 *             {@link org.jadira.usertype.dateandtime.joda.PersistentLocalTimeAsString}
 */
public class StringColumnTimeOfDayMapper extends AbstractStringColumnMapper<TimeOfDay> {

    private static final long serialVersionUID = -6885561256539185520L;

    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':').appendSecondOfMinute(2)
            .appendOptional(new DateTimeFormatterBuilder().appendLiteral('.').appendFractionOfSecond(3, 9).toParser()).toFormatter();

    public static final DateTimeFormatter LOCAL_TIME_NOSECONDS_FORMATTER = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).toFormatter();

    @Override
    public TimeOfDay fromNonNullValue(String s) {
        return new TimeOfDay(s);
    }

    @Override
    public String toNonNullValue(TimeOfDay value) {
        
        if (value.getMillisOfSecond() == 0) {
            if (value.getMinuteOfHour() == 0) {
                return LOCAL_TIME_NOSECONDS_FORMATTER.print(value);
            }
            return LOCAL_TIME_FORMATTER.print(value);
        } else {
            return value.toString().substring(1);
        }
    }
}
