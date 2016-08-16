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
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

public class StringColumnLocalDateTimeMapper extends AbstractStringColumnMapper<LocalDateTime> {

    private static final long serialVersionUID = -6885561256539185520L;

    public static final DateTimeFormatter LOCAL_DATETIME_PARSER = ISODateTimeFormat.localDateOptionalTimeParser();

    private static final DateTimeFormatter DATE_TIME_PRINTER_PREFIX = new DateTimeFormatterBuilder()
            .append(ISODateTimeFormat.yearMonth()).appendLiteral("-")
            .toFormatter();
    
    public static final DateTimeFormatter LOCAL_TIME_PRINTER = ISODateTimeFormat.hourMinuteSecond();

    public static final DateTimeFormatter LOCAL_MILLIS_PRINTER = DateTimeFormat.forPattern("SSS");

    
    @Override
    public LocalDateTime fromNonNullValue(String s) {
        return LOCAL_DATETIME_PARSER.parseDateTime(s).toLocalDateTime();
    }

    @Override
    public String toNonNullValue(LocalDateTime value) {

    	String date = DATE_TIME_PRINTER_PREFIX.print(value);
    	
    	int dayOfMonth = value.getDayOfMonth();
    	
        if (dayOfMonth < 10) {
        	date = date + "0";
        }
        date = date + dayOfMonth;
    	
    	String millis = LOCAL_MILLIS_PRINTER.print(value);
    	while (millis.length() > 1 && millis.endsWith("0")) {
    		millis = millis.substring(0, millis.length() - 1);
    	}
    	
        String formatted = date + "T" + LOCAL_TIME_PRINTER.print(value) + "." + millis;
        return formatted;
    }
}
