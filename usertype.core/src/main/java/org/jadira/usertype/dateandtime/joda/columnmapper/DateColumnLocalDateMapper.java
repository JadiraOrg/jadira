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
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class DateColumnLocalDateMapper extends AbstractDateColumnMapper<LocalDate> {

    private static final long serialVersionUID = 6734385103313158326L;

    public static final DateTimeFormatter LOCAL_DATE_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();

    private DateTimeZone databaseZone = DateTimeZone.UTC;
    
    public DateColumnLocalDateMapper() {
    }
    
    public DateColumnLocalDateMapper(DateTimeZone databaseZone) {
    	this.databaseZone = databaseZone;
    }
    
    @Override
    public LocalDate fromNonNullString(String s) {
        return new LocalDate(s);
    }

    @Override
    public LocalDate fromNonNullValue(Date value) {
    	if (databaseZone == null) {
    		return new LocalDate(value.toString());
    	} else {
    		DateTime referenceDateTime = new DateTime(value.toString(), databaseZone);
    		return referenceDateTime.toLocalDate();
    	}
    }

    @Override
    public String toNonNullString(LocalDate value) {
        return value.toString();
    }

    @Override
    public Date toNonNullValue(LocalDate value) {
        if (databaseZone==null) {
        	return Date.valueOf(LOCAL_DATE_FORMATTER.print((LocalDate) value));
        }

        DateTime referenceDateTime = value.toDateTimeAtStartOfDay(databaseZone);        
        return new Date(referenceDateTime.getMillis());
    }
    
    public void setDatabaseZone(DateTimeZone databaseZone) {
        this.databaseZone = databaseZone;
    }
}
