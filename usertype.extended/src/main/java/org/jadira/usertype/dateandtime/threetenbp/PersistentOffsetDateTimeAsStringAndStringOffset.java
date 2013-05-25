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

import org.jadira.usertype.dateandtime.threetenbp.columnmapper.StringColumnLocalDateTimeMapper;
import org.jadira.usertype.dateandtime.threetenbp.columnmapper.StringColumnZoneOffsetMapper;
import org.jadira.usertype.spi.shared.AbstractMultiColumnUserType;
import org.jadira.usertype.spi.shared.ColumnMapper;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

/**
 * Persist {@link OffsetDateTime} via Hibernate. The offset will be stored in an extra column.
 */
public class PersistentOffsetDateTimeAsStringAndStringOffset extends AbstractMultiColumnUserType<OffsetDateTime> {

    private static final long serialVersionUID = 1364221029392346011L;

    private static final ColumnMapper<?, ?>[] COLUMN_MAPPERS = new ColumnMapper<?, ?>[] { new StringColumnLocalDateTimeMapper(), new StringColumnZoneOffsetMapper() };

    private static final String[] PROPERTY_NAMES = new String[]{ "datetime", "offset" };

    @Override
    protected OffsetDateTime fromConvertedColumns(Object[] convertedColumns) {

        LocalDateTime datePart = (LocalDateTime) convertedColumns[0];
        ZoneOffset offset = (ZoneOffset) convertedColumns[1];

        return OffsetDateTime.of(datePart, offset);
    }


    @Override
    protected ColumnMapper<?, ?>[] getColumnMappers() {
        return COLUMN_MAPPERS;
    }

    @Override
    protected Object[] toConvertedColumns(OffsetDateTime value) {

        return new Object[] { value.toLocalDateTime(), value.getOffset() };
    }

    @Override
    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
    }
}
