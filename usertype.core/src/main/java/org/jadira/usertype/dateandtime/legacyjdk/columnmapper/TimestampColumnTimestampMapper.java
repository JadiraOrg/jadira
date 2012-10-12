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

/**
 * Maps a precise timestamp column for storage. The GMT Zone will be used to store the value
 */
public class TimestampColumnTimestampMapper extends AbstractVersionableTimestampColumnMapper<java.sql.Timestamp> {

    private static final long serialVersionUID = -7670411089210984705L;
    
    private static final ThreadLocal<SimpleDateFormat> DATETIME_FORMAT = new ThreadLocal<SimpleDateFormat>() {
    	@Override protected SimpleDateFormat initialValue() {
    		
    		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    		format.setTimeZone(TimeZone.getTimeZone("GMT"));
    		
    		return format;
    	}
    };
    private TimeZone databaseZone = TimeZone.getTimeZone("GMT");

    public TimestampColumnTimestampMapper() {
    }
    
    public TimestampColumnTimestampMapper(TimeZone databaseZone) {
    	this.databaseZone = databaseZone;
    }
    
    @Override
    public java.sql.Timestamp fromNonNullString(String s) {

    	return Timestamp.valueOf(s);
    }

    @Override
    public java.sql.Timestamp fromNonNullValue(Timestamp value) {

        TimeZone currentDatabaseZone = databaseZone == null ? TimeZone.getDefault() : databaseZone;

    	SimpleDateFormat format = DATETIME_FORMAT.get();
    	format.setTimeZone(currentDatabaseZone);
        
    	String timestampAsString = value.toString();
    	
    	ParsePosition parsePosition = new ParsePosition(0);
        Date date = format.parse(timestampAsString, parsePosition);
        
        if (date == null) {
        	throw new IllegalArgumentException("Could not parse date: " + timestampAsString);
        }

        int currentPosition = parsePosition.getIndex();
        int remaining = timestampAsString.length() - currentPosition;
        int nanos;

        if (remaining == 0) {
            nanos = 0;
        } else {

            if (timestampAsString.charAt(currentPosition) != '.') {
            	throw new IllegalArgumentException("Nanoseconds part was incorrectly formatted: " + timestampAsString);
            }
            
            currentPosition = currentPosition + 1;

            if (((timestampAsString.length() - currentPosition) < 1) || 
            		((timestampAsString.length() - currentPosition) > 9)) {
            	throw new IllegalArgumentException("Nanoseconds part was incorrectly formatted: " + timestampAsString);
            }

            String nanosString = timestampAsString.substring(currentPosition);
            // Pad String if necessary
            if (nanosString.length() < 9) {
            	nanosString = nanosString + "000000000";
            	nanosString = nanosString.substring(0, 9);
            }

            try {
                nanos = Integer.parseInt(nanosString);
            } catch (NumberFormatException e) {
            	throw new IllegalArgumentException("Nanoseconds part was incorrectly formatted: " + timestampAsString, e);
            }
        }

        if (nanos < 0 || nanos > 999999999) {
        	throw new IllegalArgumentException("Nanoseconds part was incorrectly formatted: " + timestampAsString);
        }

        if (nanos != 0) {
        	date.setTime(date.getTime() + (nanos / 1000000));
        }
    	
        return new Timestamp(date.getTime());
    }

    @Override
    public String toNonNullString(java.sql.Timestamp value) {

        TimeZone gmtZone = TimeZone.getTimeZone("GMT");
        
        final SimpleDateFormat sdf = DATETIME_FORMAT.get();
        sdf.setTimeZone(gmtZone);
        
        Calendar now = Calendar.getInstance(gmtZone);
        now.clear();
        now.setTime(value);
        
        final String tsString;
        
        long milliseconds = now.get(Calendar.MILLISECOND);
        if (milliseconds > 0) {
        	tsString = sdf.format(value);
        } else {
        	String nanosString = "" + milliseconds + "000000000";
        	nanosString = nanosString.substring(0, 9);
        	tsString = sdf.format(value) + "." + nanosString;
        }
    	
        return tsString;
    }

    @Override
    public Timestamp toNonNullValue(java.sql.Timestamp value) {

        TimeZone currentDatabaseZone = databaseZone == null ? TimeZone.getDefault() : databaseZone;
        
        final SimpleDateFormat sdf = DATETIME_FORMAT.get();
        sdf.setTimeZone(currentDatabaseZone);
        
        Calendar now = Calendar.getInstance(currentDatabaseZone);
        now.clear();
        now.setTime(value);
        
        final String tsString;
        
        long milliseconds = now.get(Calendar.MILLISECOND);
        if (milliseconds > 0) {
        	tsString = sdf.format(value);
        } else {
        	String nanosString = "" + milliseconds + "000000000";
        	nanosString = nanosString.substring(0, 9);
        	tsString = sdf.format(value) + "." + nanosString;
        }
        
        final Timestamp timestamp = Timestamp.valueOf(tsString);
        
		return timestamp;
    }

    public void setDatabaseZone(TimeZone databaseZone) {
        this.databaseZone = databaseZone;
    }
}
