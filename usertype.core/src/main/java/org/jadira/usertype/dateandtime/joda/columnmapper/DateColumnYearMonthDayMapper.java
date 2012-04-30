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

import java.sql.Date;

import org.jadira.usertype.spi.shared.AbstractDateColumnMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.YearMonthDay;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * @deprecated Recommend replacing use of {@link YearMonthDay} with {@link org.joda.time.LocalDate} and {@link org.jadira.usertype.dateandtime.joda.PersistentLocalDate}
 */
public class DateColumnYearMonthDayMapper extends AbstractDateColumnMapper<YearMonthDay> {

	private static final long serialVersionUID = 5399269707841091964L;

	public static final DateTimeFormatter LOCAL_DATE_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();

    private DateTimeZone databaseZone = DateTimeZone.UTC;
    
    public DateColumnYearMonthDayMapper() {
    }
    
    public DateColumnYearMonthDayMapper(DateTimeZone databaseZone) {
    	this.databaseZone = databaseZone;
    }
    
    @Override
    public YearMonthDay fromNonNullString(String s) {
        return new YearMonthDay(s);
    }
    
    @Override
    public YearMonthDay fromNonNullValue(Date value) {
    	if (databaseZone == null) {
    		return new YearMonthDay(value.toString());
    	} else {
    		DateTime referenceDateTime = new DateTime(value.toString(), databaseZone);
    		return referenceDateTime.toYearMonthDay();
    	}
    }

    @Override
    public String toNonNullString(YearMonthDay value) {
        return value.toString();
    }

    @Override
    public Date toNonNullValue(YearMonthDay value) {
        if (databaseZone==null) {
        	return Date.valueOf(LOCAL_DATE_FORMATTER.print((YearMonthDay) value));
        }

        DateTime referenceDateTime = value.toLocalDate().toDateTimeAtStartOfDay(databaseZone);        
        return new Date(referenceDateTime.getMillis());
    }
    
    public void setDatabaseZone(DateTimeZone databaseZone) {
        this.databaseZone = databaseZone;
    }
}
