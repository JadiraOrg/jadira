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

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.jadira.usertype.spi.shared.AbstractTimeColumnMapper;

public class TimeColumnLocalTimeMapper extends AbstractTimeColumnMapper<LocalTime> {

    private static final long serialVersionUID = 6734385103313158326L;
    
    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter();

	public TimeColumnLocalTimeMapper() {
	}
    
    @Override
    public LocalTime fromNonNullString(String s) {
        return LocalTime.parse(s);
    }

    @Override
    public LocalTime fromNonNullValue(Time value) {
    	
        return value.toLocalTime();
    }

    @Override
    public String toNonNullString(LocalTime value) {
        return value.toString();
    }

    @Override
    public Time toNonNullValue(LocalTime value) {
    	
    	LocalDateTime localDateTime = LocalDateTime.of(
    				1970, 1, 1, value.getHour(), value.getMinute(), value.getSecond(), value.getNano()
    			);
    	
        final Time time = new Time(localDateTime.getNano());
        return time;
    }
}
