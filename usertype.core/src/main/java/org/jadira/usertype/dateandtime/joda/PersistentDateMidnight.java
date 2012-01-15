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

import org.jadira.usertype.dateandtime.joda.columnmapper.DateColumnLocalDateMapper;
import org.jadira.usertype.dateandtime.joda.columnmapper.StringColumnDateTimeZoneMapper;
import org.jadira.usertype.spi.reflectionutils.ArrayUtils;
import org.jadira.usertype.spi.shared.ColumnMapper;

/**
 * Persist {@link org.joda.time.DateMidnight} via Hibernate. The offset will be stored in an extra column.
 */
public class PersistentDateMidnight extends AbstractMultiColumnDateMidnight {

    private static final long serialVersionUID = 1364221029392346011L;

    private static final ColumnMapper<?, ?>[] columnMappers = new ColumnMapper<?, ?>[] { new DateColumnLocalDateMapper(), new StringColumnDateTimeZoneMapper() };

    private static final String[] propertyNames = new String[] { "date", "offset" };

    @Override
    protected ColumnMapper<?, ?>[] getColumnMappers() {
        return columnMappers;
    }

    @Override
    public String[] getPropertyNames() {
        return ArrayUtils.copyOf(propertyNames);
    }
}
