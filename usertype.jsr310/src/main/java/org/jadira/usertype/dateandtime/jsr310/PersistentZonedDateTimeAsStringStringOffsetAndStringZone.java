/*
 *  Copyright 2010 Christopher Pheby
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
package org.jadira.usertype.dateandtime.jsr310;

import javax.time.calendar.LocalDateTime;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.ZonedDateTime;

import org.jadira.usertype.dateandtime.jsr310.columnmapper.StringColumnLocalDateTimeMapper;
import org.jadira.usertype.dateandtime.jsr310.columnmapper.StringColumnTimeZoneMapper;
import org.jadira.usertype.dateandtime.jsr310.columnmapper.StringColumnZoneOffsetMapper;
import org.jadira.usertype.dateandtime.shared.spi.AbstractMultiColumnUserType;
import org.jadira.usertype.dateandtime.shared.spi.ColumnMapper;


/**
 * Persist {@link ZonedDateTime} via Hibernate. The offset will be stored in an extra column.
 */
public class PersistentZonedDateTimeAsStringStringOffsetAndStringZone extends AbstractMultiColumnUserType<ZonedDateTime> {

    private static final long serialVersionUID = -1335371912886315820L;

    private static final ColumnMapper<?, ?>[] columnMappers = new ColumnMapper<?, ?>[] { new StringColumnLocalDateTimeMapper(), new StringColumnZoneOffsetMapper(), new StringColumnTimeZoneMapper() };
    
    private static final String[] propertyNames = new String[]{ "datetime", "offset", "timezone" };
    
    @Override
    protected ZonedDateTime fromConvertedColumns(Object[] convertedColumns) {

        LocalDateTime datePart = (LocalDateTime) convertedColumns[0];
        ZoneOffset offset = (ZoneOffset) convertedColumns[1];
        TimeZone timeZone = (TimeZone) convertedColumns[2];
        
        return ZonedDateTime.of(OffsetDateTime.of(datePart, offset), timeZone);
    }
  
    @Override
    protected ColumnMapper<?, ?>[] getColumnMappers() {
        return columnMappers;
    }

    @Override
    protected Object[] toConvertedColumns(ZonedDateTime value) {

        return new Object[] { value.toOffsetDateTime().toLocalDateTime(), value.toOffsetDateTime().getOffset(), value.getZone() };
    }
    
    public String[] getPropertyNames() {
        return propertyNames;
    }
}
