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

import java.sql.Timestamp;
import java.util.TimeZone;

import org.jadira.usertype.dateandtime.joda.util.ZoneHelper;
import org.jadira.usertype.spi.shared.AbstractTimestampColumnMapper;
import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

public class TimestampColumnLocalTimeMapper extends AbstractTimestampColumnMapper<LocalTime> implements DatabaseZoneConfigured<DateTimeZone> {

    private static final long serialVersionUID = 1921591625617366103L;

    private DateTimeZone databaseZone = DateTimeZone.UTC;

    public TimestampColumnLocalTimeMapper() {
    }
    
    public TimestampColumnLocalTimeMapper(DateTimeZone databaseZone) {
    	this.databaseZone = databaseZone;
    }

    @Override
    public LocalTime fromNonNullString(String s) {
        return new LocalTime(s);
    }

    @Override
    public LocalTime fromNonNullValue(Timestamp value) {

        DateTimeZone currentDatabaseZone = databaseZone == null ? ZoneHelper.getDefault() : databaseZone;
        
        int adjustment = TimeZone.getDefault().getOffset(value.getTime()) - currentDatabaseZone.getOffset(null);
        
        DateTime dateTime = new DateTime(value.getTime() + adjustment, currentDatabaseZone);
        LocalTime localTime = dateTime.toLocalTime();
        
        return localTime;
    }
    
    @Override
    public String toNonNullString(LocalTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(LocalTime value) {

        DateTimeZone currentDatabaseZone = databaseZone == null ? ZoneHelper.getDefault() : databaseZone;
    	DateTime zonedValue = new LocalDateTime(
    			1970,1,1,value.getHourOfDay(), value.getMinuteOfHour(), value.getSecondOfMinute(), value.getMillisOfSecond(), value.getChronology()
    	).toDateTime(currentDatabaseZone);
    	
        int adjustment = TimeZone.getDefault().getOffset(zonedValue.getMillis()) - currentDatabaseZone.getOffset(null);
    	
        final Timestamp timestamp = new Timestamp(zonedValue.getMillis() - adjustment);
        return timestamp;
    }

    @Override
	public void setDatabaseZone(DateTimeZone databaseZone) {
        this.databaseZone = databaseZone;
    }

	@Override
	public DateTimeZone parseZone(String zoneString) {
		return DateTimeZone.forID(zoneString);
	}
}