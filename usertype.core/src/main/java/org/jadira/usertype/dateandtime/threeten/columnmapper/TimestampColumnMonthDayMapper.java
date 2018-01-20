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
package org.jadira.usertype.dateandtime.threeten.columnmapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.jadira.usertype.spi.shared.AbstractVersionableTimestampColumnMapper;

public class TimestampColumnMonthDayMapper extends AbstractVersionableTimestampColumnMapper<MonthDay> {

    private static final long serialVersionUID = -7670411089210984705L;
    
    public static final DateTimeFormatter LOCAL_DATETIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").appendOptional(new DateTimeFormatterBuilder().appendLiteral('.').appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, false).toFormatter()).toFormatter();

	public TimestampColumnMonthDayMapper() {
		super();
	}
    
    @Override
    public MonthDay fromNonNullString(String s) {
        return MonthDay.parse(s);
    }

    @Override
    public MonthDay fromNonNullValue(Timestamp value) {
    	
    	LocalDateTime ldt = value.toLocalDateTime();
        return MonthDay.of(ldt.getMonth(), ldt.getDayOfMonth());
    }

    @Override
    public String toNonNullString(MonthDay value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(MonthDay value) {

        LocalDateTime ldt = LocalDateTime.of(1970, value.getMonthValue(), value.getDayOfMonth(), 0, 0);        
        final Timestamp timestamp = Timestamp.valueOf(ldt);
        return timestamp;
    }
}
