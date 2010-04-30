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

import org.jadira.usertype.dateandtime.joda.PersistentLocalTimeAsMillisInteger;
import org.jadira.usertype.dateandtime.shared.spi.AbstractIntegerColumnMapper;
import org.joda.time.TimeOfDay;

/**
 * @deprecated Recommend replacing use of {@link TimeOfDay} with {@link org.joda.time.LocalTime} and {@link PersistentLocalTimeAsMillisInteger}
 */
public class IntegerColumnTimeOfDayMapper extends AbstractIntegerColumnMapper<TimeOfDay> {

    private static final long serialVersionUID = -3448788221055335510L;

    @Override
    public TimeOfDay fromNonNullString(String s) {
        return new TimeOfDay(s);
    }

    @Override
    public TimeOfDay fromNonNullValue(Integer value) {
        return TimeOfDay.fromMillisOfDay(value);
    }

    @Override
    public String toNonNullString(TimeOfDay value) {
        return value.toString();
    }

    @Override
    public Integer toNonNullValue(TimeOfDay value) {
        return value.toLocalTime().millisOfDay().get();
    }
}
