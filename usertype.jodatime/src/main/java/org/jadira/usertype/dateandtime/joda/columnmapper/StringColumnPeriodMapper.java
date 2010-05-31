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
package org.jadira.usertype.dateandtime.joda.columnmapper;

import org.jadira.usertype.dateandtime.shared.spi.AbstractStringColumnMapper;
import org.joda.time.Period;

public class StringColumnPeriodMapper extends AbstractStringColumnMapper<Period> {

    private static final long serialVersionUID = -5741261927204773374L;

    @Override
    public Period fromNonNullValue(String s) {
        return new Period(s);
    }

    @Override
    public String toNonNullValue(Period value) {
        return value.toString();
    }
}
