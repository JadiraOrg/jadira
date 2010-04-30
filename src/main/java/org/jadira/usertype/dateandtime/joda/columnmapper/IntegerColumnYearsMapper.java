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

import org.jadira.usertype.dateandtime.shared.spi.AbstractIntegerColumnMapper;
import org.joda.time.Years;

public class IntegerColumnYearsMapper extends AbstractIntegerColumnMapper<Years> {

    private static final long serialVersionUID = 3803107030453775035L;

    @Override
    public Years fromNonNullString(String s) {
        return Years.years(Integer.parseInt(s));
    }

    @Override
    public Years fromNonNullValue(Integer value) {
        return Years.years(value);
    }

    @Override
    public String toNonNullString(Years value) {
        return "" + value.getYears();
    }

    @Override
    public Integer toNonNullValue(Years value) {
        return value.getYears();
    }
}
