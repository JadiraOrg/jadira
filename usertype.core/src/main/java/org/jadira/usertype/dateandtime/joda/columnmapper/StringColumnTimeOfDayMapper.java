/*
 * Copyright 2010, 2011 Christopher Pheby Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package org.jadira.usertype.dateandtime.joda.columnmapper;

import org.jadira.usertype.dateandtime.joda.util.Formatter;
import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;
import org.joda.time.TimeOfDay;

/**
 * @deprecated Recommend replacing use of {@link TimeOfDay} with {@link org.joda.time.LocalTime} and
 *             {@link org.jadira.usertype.dateandtime.joda.PersistentLocalTimeAsString}
 */
public class StringColumnTimeOfDayMapper extends AbstractStringColumnMapper<TimeOfDay> {

    private static final long serialVersionUID = 1169024711030425531L;

    @Override
    public TimeOfDay fromNonNullValue(String s) {
        return new TimeOfDay(s);
    }

    @Override
    public String toNonNullValue(TimeOfDay value) {

        if (value.getMillisOfSecond() == 0) {
            if (value.getMinuteOfHour() == 0) {
                return Formatter.LOCAL_TIME_NOSECONDS_FORMATTER.print(value);
            }
            return Formatter.LOCAL_TIME_FORMATTER.print(value);
        } else {
            return value.toString().substring(1);
        }
    }
}
