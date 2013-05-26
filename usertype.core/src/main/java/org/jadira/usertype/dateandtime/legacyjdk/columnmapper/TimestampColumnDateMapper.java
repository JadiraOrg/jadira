/*
 *  Copyright 2010, 2011, 2012 Christopher Pheby
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
package org.jadira.usertype.dateandtime.legacyjdk.columnmapper;

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.jadira.usertype.spi.shared.AbstractVersionableTimestampColumnMapper;
import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;

/**
 * Maps a precise date column for storage. The GMT Zone will be used to store the value
 */
public class TimestampColumnDateMapper extends AbstractVersionableTimestampColumnMapper<java.util.Date> implements DatabaseZoneConfigured<TimeZone> {

    private static final long serialVersionUID = -7670411089210984705L;

	protected static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    
    private static final ThreadLocal<SimpleDateFormat> DATETIME_FORMAT = new ThreadLocal<SimpleDateFormat>() {
    	@Override protected SimpleDateFormat initialValue() {
    		
    		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    		format.setTimeZone(GMT);
    		
    		return format;
    	}
    };
    private TimeZone databaseZone = GMT;

    public TimestampColumnDateMapper() {
    }
    
    public TimestampColumnDateMapper(TimeZone databaseZone) {
    	this.databaseZone = databaseZone;
    }
    
    @Override
    public java.util.Date fromNonNullString(String s) {

    	java.util.Date date = null;
    	
        // remove whitespace
        s = s.trim();
    	
    	String mainPart = s.substring(0, 23);
    	ParsePosition parsePosition = new ParsePosition(0);
    	
    	SimpleDateFormat format = DATETIME_FORMAT.get();
    	format.setTimeZone(GMT);
        date = format.parse(mainPart, parsePosition);
        
        if (date == null) {
        	throw new IllegalArgumentException("Could not parse date: " + s);
        }

        int currentPosition = parsePosition.getIndex();
        int remaining = s.length() - currentPosition;
        int nanos;

        if (remaining == 0) {
            nanos = 0;
        } else {

            if (s.charAt(currentPosition) != '.') {
            	throw new IllegalArgumentException("Nanoseconds part was incorrectly formatted: " + s);
            }
            
            currentPosition = currentPosition + 1;

            if (((s.length() - currentPosition) < 1) || 
            		((s.length() - currentPosition) > 9)) {
            	throw new IllegalArgumentException("Nanoseconds part was incorrectly formatted: " + s);
            }

            String nanosString = s.substring(currentPosition);
            // Pad String if necessary
            if (nanosString.length() < 9) {
            	nanosString = nanosString + "000000000";
            	nanosString = nanosString.substring(0, 9);
            }

            try {
                nanos = Integer.parseInt(nanosString);
            } catch (NumberFormatException e) {
            	throw new IllegalArgumentException("Nanoseconds part was incorrectly formatted: " + s, e);
            }
        }

        if (nanos < 0 || nanos > 999999999) {
        	throw new IllegalArgumentException("Nanoseconds part was incorrectly formatted: " + s);
        }

        if (nanos != 0) {
        	date.setTime(date.getTime() + (nanos / 1000000));
        }
        
        return date;
    }

    @Override
    public java.util.Date fromNonNullValue(Timestamp value) {

    	TimeZone currentDatabaseZone = databaseZone == null ? TimeZone.getDefault() : databaseZone;

        int adjustment = TimeZone.getDefault().getOffset(value.getTime()) - currentDatabaseZone.getOffset(new Date().getTime());
        
        Date date = new Date(value.getTime() + adjustment);
        return date;
    }

    @Override
    public String toNonNullString(java.util.Date value) {

        TimeZone gmtZone = GMT;
        
        final SimpleDateFormat sdf = DATETIME_FORMAT.get();
        sdf.setTimeZone(gmtZone);
        
        Calendar now = Calendar.getInstance(gmtZone);
        now.clear();
        now.setTime(value);
        
        final String tsString;
        
        long milliseconds = now.get(Calendar.MILLISECOND);
        if (milliseconds == 0) {
        	tsString = sdf.format(value);
        } else {
        	String nanosString = "" + milliseconds + "000000000";
        	nanosString = nanosString.substring(0, 9);
        	tsString = sdf.format(value) + "." + nanosString;
        }
    	
        return tsString;
    }

    @Override
    public Timestamp toNonNullValue(java.util.Date value) {

    	TimeZone currentDatabaseZone = databaseZone == null ? TimeZone.getDefault() : databaseZone;
        
        int adjustment = TimeZone.getDefault().getOffset(value.getTime()) - currentDatabaseZone.getOffset(new Date().getTime());
        
        final Timestamp timestamp = new Timestamp(value.getTime() - adjustment);
        return timestamp;
    }

    @Override
    public void setDatabaseZone(TimeZone databaseZone) {
        this.databaseZone = databaseZone;
    }
        
	@Override
	public TimeZone parseZone(String zoneString) {
		return TimeZone.getTimeZone(zoneString);
	}
}
