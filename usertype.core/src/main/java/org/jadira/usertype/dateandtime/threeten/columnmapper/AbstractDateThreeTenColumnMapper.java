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

import org.jadira.usertype.spi.shared.AbstractDateColumnMapper;
import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.jadira.usertype.spi.shared.DstSafeDateType;
import java.time.ZoneId;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public abstract class AbstractDateThreeTenColumnMapper<T> extends AbstractDateColumnMapper<T> implements DatabaseZoneConfigured<ZoneId> {

    private static final long serialVersionUID = -7670411089210984705L;
	
    /* Explicitly set this to null for preferred default behaviour. See https://jadira.atlassian.net/browse/JDF-26 */
    private ZoneId databaseZone = null;
    
	public AbstractDateThreeTenColumnMapper() {
	}

	public AbstractDateThreeTenColumnMapper(ZoneId databaseZone) {
		this.databaseZone = databaseZone;
	}

    
    @Override
    public void setDatabaseZone(ZoneId databaseZone) {
        this.databaseZone = databaseZone;
    }

    protected ZoneId getDatabaseZone() {
        return databaseZone;
    }
	
    @Override
    public final DstSafeDateType getHibernateType() {
    	
    	if (databaseZone == null) {
    		return DstSafeDateType.INSTANCE;
    	}
    	
    	Calendar cal = resolveCalendar(databaseZone);
    	if (cal == null) {
    		throw new IllegalStateException("Could not map Zone " + databaseZone + " to Calendar");
    	}
    	
    	return new DstSafeDateType(cal);
    }

	private Calendar resolveCalendar(ZoneId databaseZone) {
		
		String id = databaseZone.getId();
		if (Arrays.binarySearch(TimeZone.getAvailableIDs(), id) != -1) {
			return Calendar.getInstance(TimeZone.getTimeZone(id));
		} else {
			return null;
		}
	}
}
