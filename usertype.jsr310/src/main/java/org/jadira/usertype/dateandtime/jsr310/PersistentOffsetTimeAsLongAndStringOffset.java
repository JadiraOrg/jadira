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
package org.jadira.usertype.dateandtime.jsr310;

import javax.time.calendar.LocalTime;
import javax.time.calendar.OffsetTime;
import javax.time.calendar.ZoneOffset;

import org.jadira.usertype.dateandtime.jsr310.columnmapper.LongColumnLocalTimeMapper;
import org.jadira.usertype.dateandtime.jsr310.columnmapper.StringColumnZoneOffsetMapper;
import org.jadira.usertype.dateandtime.shared.spi.AbstractMultiColumnUserType;
import org.jadira.usertype.dateandtime.shared.spi.ColumnMapper;


/**
 * Persist {@link OffsetTime} via Hibernate using nanoseconds of the day. This uses a long value stored as nanoseconds
 * in the database and a String for offset.
 */
public class PersistentOffsetTimeAsLongAndStringOffset extends AbstractMultiColumnUserType<OffsetTime> {

    private static final long serialVersionUID = 1364221029392346011L;

    private static final ColumnMapper<?, ?>[] columnMappers = new ColumnMapper<?, ?>[] { new LongColumnLocalTimeMapper(), new StringColumnZoneOffsetMapper() };

    private static final String[] propertyNames = new String[]{ "time", "offset" };

    @Override
    protected OffsetTime fromConvertedColumns(Object[] convertedColumns) {

        LocalTime datePart = (LocalTime) convertedColumns[0];
        ZoneOffset offset = (ZoneOffset) convertedColumns[1];

        return OffsetTime.of(datePart, offset);
    }


    @Override
    protected ColumnMapper<?, ?>[] getColumnMappers() {
        return columnMappers;
    }

    @Override
    protected Object[] toConvertedColumns(OffsetTime value) {

        return new Object[] { value.toLocalTime(), value.getOffset() };
    }

    public String[] getPropertyNames() {
        return propertyNames;
    }
}
