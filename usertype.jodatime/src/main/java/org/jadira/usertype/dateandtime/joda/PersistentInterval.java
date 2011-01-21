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
package org.jadira.usertype.dateandtime.joda;

import org.jadira.usertype.dateandtime.joda.columnmapper.TimestampColumnLocalDateTimeMapper;
import org.jadira.usertype.dateandtime.shared.spi.AbstractMultiColumnUserType;
import org.jadira.usertype.dateandtime.shared.spi.ColumnMapper;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;

/**
 * Persist {@link Interval} via Hibernate. The interval type is intended to be compatible with
 * {@link org.joda.time.contrib.hibernate.PersistentInterval} and stores beginning and end values
 * using the UTC zone.
 */
public class PersistentInterval extends AbstractMultiColumnUserType<Interval> {

    private static final long serialVersionUID = 1364221029392346011L;

    private static final ColumnMapper<?, ?>[] columnMappers = new ColumnMapper<?, ?>[] { new TimestampColumnLocalDateTimeMapper(), new TimestampColumnLocalDateTimeMapper() };
    
    private static final String[] propertyNames = new String[]{ "begin", "end" };
    
    @Override
    protected Interval fromConvertedColumns(Object[] convertedColumns) {

        LocalDateTime begin = (LocalDateTime) convertedColumns[0];
        LocalDateTime end = (LocalDateTime) convertedColumns[1];
        
        return new Interval(begin.toDateTime(DateTimeZone.UTC), end.toDateTime(DateTimeZone.UTC));
    }

    @Override
    protected ColumnMapper<?, ?>[] getColumnMappers() {
        return columnMappers;
    }

    @Override
    protected Object[] toConvertedColumns(Interval value) {

        return new Object[] { value.getStart().toLocalDateTime(), value.getEnd().toLocalDateTime() };
    }
    
    public String[] getPropertyNames() {
        return propertyNames;
    }
}
