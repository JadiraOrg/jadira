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

import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

public class StringColumnInstantMapper extends AbstractStringColumnMapper<Instant> {

    private static final long serialVersionUID = -6885561256539185520L;

    public static final DateTimeFormatter LOCAL_DATETIME_FORMATTER = new DateTimeFormatterBuilder()
        .append(ISODateTimeFormat.date())
        .appendLiteral('T').appendHourOfDay(2)
        .appendLiteral(':').appendMinuteOfHour(2)
        .appendLiteral(':').appendSecondOfMinute(2)
        .appendOptional(new DateTimeFormatterBuilder().appendLiteral('.').appendFractionOfSecond(3, 9).toParser())
        .appendTimeZoneOffset("Z", true, 2, 4).toFormatter();

    @Override
    public Instant fromNonNullValue(String s) {
        return LOCAL_DATETIME_FORMATTER.parseDateTime(s).toInstant();
    }

    @Override
    public String toNonNullValue(Instant value) {

        String formatted = ISODateTimeFormat.dateTime().print(value);
        if (formatted.endsWith(".000Z")) {
            formatted = formatted.substring(0, formatted.length() - 5) + "Z";
        }
        return formatted;
    }
}
