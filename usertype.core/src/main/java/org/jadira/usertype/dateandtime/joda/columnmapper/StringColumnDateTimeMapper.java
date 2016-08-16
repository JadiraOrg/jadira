/*
 *  Copyright 2013 Christopher Pheby
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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Maps {@link DateTime} as one single, sorted string. See
 * {@link org.jadira.usertype.dateandtime.joda.PersistentDateTimeAsUtcString PersistentDateTimeAsUtcString} for more details.
 * @author dwijnand
 */
public class StringColumnDateTimeMapper extends AbstractStringColumnMapper<DateTime> {

    private static final long serialVersionUID = -2548824513686423324L;

    private static final DateTimeFormatter DATE_TIME_PARSER = new DateTimeFormatterBuilder()
        .append(ISODateTimeFormat.date())
        .appendLiteral('T')
        .append(ISODateTimeFormat.hourMinuteSecondFraction())
        .appendLiteral('_')
        .appendTimeZoneId()
        .toFormatter();

   
    private static final DateTimeFormatter DATE_TIME_PRINTER_PREFIX = new DateTimeFormatterBuilder()
            .append(ISODateTimeFormat.yearMonth()).appendLiteral("-")
            .toFormatter();
    
    private static final DateTimeFormatter DATE_TIME_PRINTER_SUFFIX = new DateTimeFormatterBuilder()
		    .appendLiteral('T')
		    .append(ISODateTimeFormat.hourMinuteSecondFraction())
		    .appendLiteral('_')
		    .appendTimeZoneId()
		    .toFormatter();
    
    @Override
    public DateTime fromNonNullValue(String s) {

        DateTime parsedDateTime = DATE_TIME_PARSER.parseDateTime(s);
        DateTimeZone correctTimeZone = parsedDateTime.getZone();
        DateTime utcDateTime = parsedDateTime.withZoneRetainFields(DateTimeZone.UTC);
        DateTime correctedDateTime = utcDateTime.withZone(correctTimeZone);
        return correctedDateTime;
    }

    @Override
    public String toNonNullValue(DateTime value) {

        DateTimeZone correctTimeZone = value.getZone();
        DateTime utcDateTime = value.withZone(DateTimeZone.UTC);
        DateTime utcDateTimeWithCorrectTimeZone = utcDateTime.withZoneRetainFields(correctTimeZone);
        
        int dayOfMonth = utcDateTimeWithCorrectTimeZone.getDayOfMonth();
        
        String dateTimeAsString = DATE_TIME_PRINTER_PREFIX.print(utcDateTimeWithCorrectTimeZone);
        
        if (dayOfMonth < 10) {
        	dateTimeAsString = dateTimeAsString + "0";
        }
        dateTimeAsString = dateTimeAsString + dayOfMonth;
        
        dateTimeAsString = dateTimeAsString + DATE_TIME_PRINTER_SUFFIX.print(utcDateTimeWithCorrectTimeZone);
        
        return dateTimeAsString;
    }
}
