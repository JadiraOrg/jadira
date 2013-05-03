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
import org.jadira.usertype.spi.shared.AbstractVersionableTimestampColumnMapper;
import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

public class TimestampColumnInstantMapper extends AbstractVersionableTimestampColumnMapper<Instant> implements DatabaseZoneConfigured<DateTimeZone> {

    private static final long serialVersionUID = -7670411089210984705L;

    private DateTimeZone databaseZone = DateTimeZone.UTC;

    public TimestampColumnInstantMapper() {
    }
    
    public TimestampColumnInstantMapper(DateTimeZone databaseZone) {
    	this.databaseZone = databaseZone;
    }
    
    @Override
    public Instant fromNonNullString(String s) {
        return new Instant(s);
    }

    @Override
    public Instant fromNonNullValue(Timestamp value) {

        DateTimeZone currentDatabaseZone = databaseZone == null ? ZoneHelper.getDefault() : databaseZone;

        int adjustment = TimeZone.getDefault().getOffset(value.getTime()) - currentDatabaseZone.getOffset(null);
        
        Instant instant = new Instant(value.getTime() + adjustment);       
        return instant;
    }

    @Override
    public String toNonNullString(Instant value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(Instant value) {
        
        DateTimeZone currentDatabaseZone = databaseZone == null ? ZoneHelper.getDefault() : databaseZone;
        
        int adjustment = TimeZone.getDefault().getOffset(value.getMillis()) - currentDatabaseZone.getOffset(null);
        
        final Timestamp timestamp = new Timestamp(value.getMillis() - adjustment);
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
