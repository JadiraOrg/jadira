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

import javax.time.calendar.LocalDate;
import javax.time.calendar.OffsetDate;
import javax.time.calendar.ZoneOffset;

import org.jadira.usertype.dateandtime.jsr310.columnmapper.DateColumnLocalDateMapper;
import org.jadira.usertype.dateandtime.jsr310.columnmapper.StringColumnZoneOffsetMapper;
import org.jadira.usertype.dateandtime.shared.spi.AbstractMultiColumnUserType;
import org.jadira.usertype.dateandtime.shared.spi.ColumnMapper;

/**
 * Persist {@link OffsetDate} via Hibernate. The offset will be stored in an extra column.
 */
public class PersistentOffsetDateAsDateAndStringOffset extends AbstractMultiColumnUserType<OffsetDate> {

    private static final long serialVersionUID = 1364221029392346011L;

    private static final ColumnMapper<?, ?>[] columnMappers = new ColumnMapper<?, ?>[] { new DateColumnLocalDateMapper(), new StringColumnZoneOffsetMapper() };
    
    @Override
    protected OffsetDate fromConvertedColumns(Object[] convertedColumns) {

        LocalDate datePart = (LocalDate) convertedColumns[0];
        ZoneOffset offset = (ZoneOffset) convertedColumns[1];
        
        return OffsetDate.of(datePart, offset);
    }
  

    @Override
    protected ColumnMapper<?, ?>[] getColumnMappers() {
        return columnMappers;
    }

    @Override
    protected Object[] toConvertedColumns(OffsetDate value) {

        return new Object[] { value.toLocalDate(), value.getOffset() };
    }
}
