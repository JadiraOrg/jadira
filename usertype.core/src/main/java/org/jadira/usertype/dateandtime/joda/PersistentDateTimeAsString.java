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

import org.jadira.usertype.dateandtime.joda.columnmapper.StringColumnDateTimeZoneWithOffsetMapper;
import org.jadira.usertype.dateandtime.joda.columnmapper.StringColumnLocalDateTimeMapper;
import org.jadira.usertype.dateandtime.joda.util.DateTimeZoneWithOffset;
import org.jadira.usertype.spi.shared.AbstractParameterizedTemporalMultiColumnUserType;
import org.jadira.usertype.spi.shared.ColumnMapper;
import org.jadira.usertype.spi.utils.reflection.ArrayUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

/**
 * Persist {@link org.joda.time.DateTime} via Hibernate. The offset will be stored in an extra column.
 */
public class PersistentDateTimeAsString extends AbstractParameterizedTemporalMultiColumnUserType<DateTime> {

    private static final long serialVersionUID = 1364221029397346011L;

    private static final ColumnMapper<?, ?>[] COLUMN_MAPPERS = new ColumnMapper<?, ?>[] { new StringColumnLocalDateTimeMapper(), new StringColumnDateTimeZoneWithOffsetMapper() };

    private static final String[] PROPERTY_NAMES = new String[]{ "datetime", "offset" };

    @Override
    protected ColumnMapper<?, ?>[] getColumnMappers() {
        return COLUMN_MAPPERS;
    }

    @Override
    public String[] getPropertyNames() {
        return ArrayUtils.copyOf(PROPERTY_NAMES);
    }

    @Override
    protected DateTime fromConvertedColumns(Object[] convertedColumns) {

        LocalDateTime datePart = (LocalDateTime) convertedColumns[0];
        DateTimeZoneWithOffset offset = (DateTimeZoneWithOffset) convertedColumns[1];

        DateTime result;

        if (datePart == null) {
            result = null;
        } else {
            result = datePart.toDateTime(offset.getStandardDateTimeZone());            
        }
        
        // Handling DST rollover
        if (result != null && offset.getOffsetDateTimeZone() != null &&
        		offset.getStandardDateTimeZone().getOffset(result) > offset.getOffsetDateTimeZone().getOffset(result)) {
        	return result.withLaterOffsetAtOverlap();
        }

        return result;
    }

    @Override
    protected Object[] toConvertedColumns(DateTime value) {

        return new Object[] { value.toLocalDateTime(), new DateTimeZoneWithOffset(value.getZone(), value.getZone().isFixed() ? null : DateTimeZone.forOffsetMillis(value.getZone().getOffset(value))) };
    }
}
