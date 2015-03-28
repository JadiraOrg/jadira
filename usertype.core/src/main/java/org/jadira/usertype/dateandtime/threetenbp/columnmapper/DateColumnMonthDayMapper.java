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
package org.jadira.usertype.dateandtime.threetenbp.columnmapper;

import java.sql.Date;

import org.jadira.usertype.dateandtime.threetenbp.columnmapper.AbstractDateThreeTenBPColumnMapper;
import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.MonthDay;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;

public class DateColumnMonthDayMapper extends AbstractDateThreeTenBPColumnMapper<MonthDay> implements DatabaseZoneConfigured<ZoneOffset> {

    private static final long serialVersionUID = 6734385103313158326L;

    public static final DateTimeFormatter LOCAL_DATE_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();

//	private static final int MILLIS_IN_SECOND = 1000;
    
	public DateColumnMonthDayMapper() {
	}

	public DateColumnMonthDayMapper(ZoneOffset databaseZone) {
		super(databaseZone);
	}
    
    @Override
    public MonthDay fromNonNullString(String s) {
        return MonthDay.parse(s);
    }

    @Override
    public MonthDay fromNonNullValue(Date value) {

		if (getDatabaseZone() == null) {
    		return MonthDay.parse(value.toString(), LOCAL_DATE_FORMATTER);
    	}

		ZoneOffset currentDatabaseZone = getDatabaseZone() == null ? getDefault() : getDatabaseZone();
        
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.getTime()), currentDatabaseZone);
        MonthDay localDate = MonthDay.of(dateTime.getMonth(), dateTime.getDayOfMonth());
        
        return localDate;
    }

    @Override
    public String toNonNullString(MonthDay value) {
        return value.toString();
    }

    @Override
    public Date toNonNullValue(MonthDay value) {
    	
        if (getDatabaseZone() == null) {
        	return Date.valueOf(LOCAL_DATE_FORMATTER.format((MonthDay) value));
        }
    	
        LocalDate ldt = LocalDate.of(1970, value.getMonthValue(), value.getDayOfMonth());
        
		ZoneOffset currentDatabaseZone = getDatabaseZone() == null ? getDefault() : getDatabaseZone();
    	ZonedDateTime zonedValue = ldt.atStartOfDay(currentDatabaseZone);

//        Instant valueAsInstant = Instant.ofEpochSecond(zonedValue.toEpochSecond());
//        int defaultTimezoneOffset = TimeZone.getDefault().getOffset(zonedValue.toEpochSecond() * MILLIS_IN_SECOND);
//        int databaseTimezoneOffsetInSeconds = currentDatabaseZone.getRules().getOffset(valueAsInstant).getTotalSeconds();
//        int adjustment = defaultTimezoneOffset - databaseTimezoneOffsetInSeconds * MILLIS_IN_SECOND;
    	
    	final Date date = new Date(zonedValue.toInstant().toEpochMilli());
        return date;
    }

    private static ZoneOffset getDefault() {

    	ZoneOffset zone = null;
        try {
            try {
                String id = System.getProperty("user.timezone");
                if (id != null) {
                    zone = ZoneOffset.of(id);
                }
            } catch (RuntimeException ex) {
                zone = null;
            }
            if (zone == null) {
                zone = ZoneOffset.of(java.util.TimeZone.getDefault().getID());
            }
        } catch (RuntimeException ex) {
            zone = null;
        }
        if (zone == null) {
            zone = ZoneOffset.of("Z");
        }
        return zone;
    }
    
	@Override
	public ZoneOffset parseZone(String zoneString) {
		return ZoneOffset.of(zoneString);
	}
}
