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
package org.jadira.usertype.dateandtime.threetenbp;

import org.jadira.usertype.dateandtime.threetenbp.columnmapper.StringColumnLocalTimeMapper;
import org.jadira.usertype.dateandtime.threetenbp.columnmapper.StringColumnZoneOffsetMapper;
import org.jadira.usertype.spi.shared.AbstractMultiColumnUserType;
import org.jadira.usertype.spi.shared.ColumnMapper;
import org.threeten.bp.LocalTime;
import org.threeten.bp.OffsetTime;
import org.threeten.bp.ZoneOffset;

/**
 * Persist {@link OffsetTime} via Hibernate using nanoseconds of the day. This uses a long value stored as nanoseconds
 * in the database and a String for offset.
 */
public class PersistentOffsetTimeAsStringAndStringOffset extends AbstractMultiColumnUserType<OffsetTime> {

    private static final long serialVersionUID = 1364221029392346011L;

    private static final ColumnMapper<?, ?>[] COLUMN_MAPPERS = new ColumnMapper<?, ?>[] { new StringColumnLocalTimeMapper(), new StringColumnZoneOffsetMapper() };

    private static final String[] PROPERTY_NAMES = new String[]{ "time", "offset" };

    @Override
    protected OffsetTime fromConvertedColumns(Object[] convertedColumns) {

        LocalTime datePart = (LocalTime) convertedColumns[0];
        ZoneOffset offset = (ZoneOffset) convertedColumns[1];

        return OffsetTime.of(datePart, offset);
    }


    @Override
    protected ColumnMapper<?, ?>[] getColumnMappers() {
        return COLUMN_MAPPERS;
    }

    @Override
    protected Object[] toConvertedColumns(OffsetTime value) {

        return new Object[] { value.toLocalTime(), value.getOffset() };
    }

    @Override
    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
    }
}
