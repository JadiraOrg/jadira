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
import org.threeten.bp.Month;

public class StringColumnMonthMapper extends AbstractStringColumnMapper<Month> {

    private static final long serialVersionUID = 982411452349850753L;

    @Override
    public Month fromNonNullValue(String s) {

        if ("JANUARY".equals(s)) {
            return Month.JANUARY;
        } else if ("FEBRUARY".equals(s)) {
            return Month.FEBRUARY;
        } else if ("MARCH".equals(s)) {
            return Month.MARCH;
        } else if ("APRIL".equals(s)) {
            return Month.APRIL;
        } else if ("MAY".equals(s)) {
            return Month.MAY;
        } else if ("JUNE".equals(s)) {
            return Month.JUNE;
        } else if ("JULY".equals(s)) {
            return Month.JULY;
        } else if ("AUGUST".equals(s)) {
            return Month.AUGUST;
        } else if ("SEPTEMBER".equals(s)) {
            return Month.SEPTEMBER;
        } else if ("OCTOBER".equals(s)) {
            return Month.OCTOBER;
        } else if ("NOVEMBER".equals(s)) {
            return Month.NOVEMBER;
        } else if ("DECEMBER".equals(s)) {
            return Month.DECEMBER;
        } else {
            throw new IllegalArgumentException("Seen unexpected Month: " + s);
        }
    }

    @Override
    public String toNonNullValue(Month value) {
        return value.toString();
    }
}
