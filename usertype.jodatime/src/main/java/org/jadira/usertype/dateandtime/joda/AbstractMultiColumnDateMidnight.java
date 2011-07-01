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

import org.jadira.usertype.dateandtime.shared.spi.AbstractMultiColumnUserType;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

/**
 * Persist {@link DateMidnight} via Hibernate. The offset will be stored in an extra column.
 */
public abstract class AbstractMultiColumnDateMidnight extends AbstractMultiColumnUserType<DateMidnight> {

    private static final long serialVersionUID = 7061588330446583269L;

    @Override
    protected DateMidnight fromConvertedColumns(Object[] convertedColumns) {

        LocalDate datePart = (LocalDate) convertedColumns[0];
        DateTimeZone offset = (DateTimeZone) convertedColumns[1];

        final DateMidnight result;

        if (datePart == null) {
            result = null;
        } else {
            result = new DateMidnight(datePart.getYear(), datePart.getMonthOfYear(), datePart.getDayOfMonth(), offset);
        }

        return result;
    }

    @Override
    protected Object[] toConvertedColumns(DateMidnight value) {

        return new Object[] { value.toLocalDate(), value.getZone() };
    }
}
