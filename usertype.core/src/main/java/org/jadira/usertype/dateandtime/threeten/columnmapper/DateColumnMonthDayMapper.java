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

import java.sql.Date;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.jadira.usertype.spi.shared.AbstractDateColumnMapper;

public class DateColumnMonthDayMapper extends AbstractDateColumnMapper<MonthDay> {

    private static final long serialVersionUID = 6734385103313158326L;

    public static final DateTimeFormatter LOCAL_DATE_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();

//	private static final int MILLIS_IN_SECOND = 1000;
    
	public DateColumnMonthDayMapper() {
	}
    
    @Override
    public MonthDay fromNonNullString(String s) {
        return MonthDay.parse(s);
    }

    @Override
    public MonthDay fromNonNullValue(Date value) {

    	return MonthDay.parse(value.toString(), LOCAL_DATE_FORMATTER);
    }

    @Override
    public String toNonNullString(MonthDay value) {
        return value.toString();
    }

    @Override
    public Date toNonNullValue(MonthDay value) {
    	
        return Date.valueOf(LOCAL_DATE_FORMATTER.format((MonthDay) value));
    }
}
