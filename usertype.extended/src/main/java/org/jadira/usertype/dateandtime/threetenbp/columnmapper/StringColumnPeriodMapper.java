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
package org.jadira.usertype.dateandtime.threetenbp.columnmapper;

import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;
import org.threeten.bp.Period;

public class StringColumnPeriodMapper extends AbstractStringColumnMapper<Period> {

    private static final long serialVersionUID = -5741261927204773374L;

    @Override
    public Period fromNonNullValue(String s) {
        return Period.parse(s);
    }

    @Override
    public String toNonNullValue(Period value) {
        return toString((Period) value);
    }

    /**
     * Returns a string representation of the amount of time.
     * @return the amount of time in ISO8601 string format
     */
    public String toString(Period value) {

        final String str;
        if (value.isZero()) {
            str = "PT0S";
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append('P');
            if (value.getYears() != 0) {
                buf.append(value.getYears()).append('Y');
            }
            if (value.getMonths() != 0) {
                buf.append(value.getMonths()).append('M');
            }
            if (value.getDays() != 0) {
                buf.append(value.getDays()).append('D');
            }
            str = buf.toString();
        }
        return str;
    }
}
