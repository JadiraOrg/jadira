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
package org.jadira.usertype.dateandtime.joda;

import org.jadira.usertype.dateandtime.joda.columnmapper.StringColumnDateTimeZoneMapper;
import org.jadira.usertype.dateandtime.joda.columnmapper.TimestampColumnLocalDateTimeMapper;
import org.jadira.usertype.spi.shared.AbstractParameterizedMultiColumnUserType;
import org.jadira.usertype.spi.shared.ColumnMapper;
import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.jadira.usertype.spi.utils.reflection.ArrayUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

/**
 * Persist {@link org.joda.time.DateTime} via Hibernate. The offset will be stored in an extra column.
 */
public class PersistentDateTimeAndZone extends AbstractParameterizedMultiColumnUserType<DateTime> implements DatabaseZoneConfigured<DateTimeZone> {

    private static final long serialVersionUID = 1364221029392346011L;

    private static final ColumnMapper<?, ?>[] COLUMN_MAPPERS = new ColumnMapper<?, ?>[] { new TimestampColumnLocalDateTimeMapper(), new StringColumnDateTimeZoneMapper() };

    private static final String[] PROPERTY_NAMES = new String[]{ "datetime", "offset" };

    private DateTimeZone databaseZone = DateTimeZone.UTC;

    @Override
    protected DateTime fromConvertedColumns(Object[] convertedColumns) {

        LocalDateTime datePart = (LocalDateTime) convertedColumns[0];
        DateTimeZone zone = (DateTimeZone) convertedColumns[1];

        DateTime result;

        if (datePart == null) {
            result = null;
        } else {
            result = datePart.toDateTime(databaseZone == null ? zone : databaseZone);
            
            if (databaseZone != null) {
            	result = result.withZone(zone);
            }
        }

        return result;
    }

    @Override
    protected Object[] toConvertedColumns(DateTime value) {

    	final DateTime myValue;
    	if (databaseZone == null) {
    		myValue = value;
    	} else {
    		myValue = value.withZone(databaseZone);
    	}
        return new Object[] { myValue.toLocalDateTime(), value.getZone() };
    }
    
	@Override
	public void setDatabaseZone(DateTimeZone databaseZone) {
		this.databaseZone = databaseZone;
	}

	@Override
	public DateTimeZone parseZone(String zoneString) {
		return DateTimeZone.forID(zoneString);
	}
    
    @Override
    protected ColumnMapper<?, ?>[] getColumnMappers() {
        return COLUMN_MAPPERS;
    }

    @Override
    public String[] getPropertyNames() {
        return ArrayUtils.copyOf(PROPERTY_NAMES);
    }
}
