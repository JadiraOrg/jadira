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
package org.jadira.usertype.dateandtime.joda;

import org.jadira.usertype.dateandtime.shared.spi.AbstractMultiColumnUserType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

public abstract class AbstractMultiColumnDateTime extends AbstractMultiColumnUserType<DateTime> {

    private static final long serialVersionUID = 726719808559368002L;

    @Override
    protected DateTime fromConvertedColumns(Object[] convertedColumns) {

        LocalDateTime datePart = (LocalDateTime) convertedColumns[0];
        DateTimeZone offset = (DateTimeZone) convertedColumns[1];

        final DateTime result;

        if (datePart == null) {
            result = null;
        } else {
            result = new DateTime(
                    datePart.getYear(),
                    datePart.getMonthOfYear(),
                    datePart.getDayOfMonth(),
                    datePart.getHourOfDay(),
                    datePart.getMinuteOfHour(),
                    datePart.getSecondOfMinute(),
                    datePart.getMillisOfSecond(),
                    offset);
        }

        return result;
    }

    @Override
    protected Object[] toConvertedColumns(DateTime value) {

        return new Object[] { value.toLocalDateTime(), value.getZone() };
    }
}
