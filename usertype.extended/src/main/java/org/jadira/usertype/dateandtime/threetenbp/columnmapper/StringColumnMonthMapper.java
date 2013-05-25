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
    	
    	switch(s) {
    		case "JANUARY" : return Month.JANUARY;
    		case "FEBRUARY" : return Month.FEBRUARY;
    		case "MARCH" : return Month.MARCH;
    		case "APRIL" : return Month.APRIL;
    		case "MAY" : return Month.MAY;
    		case "JUNE" : return Month.JUNE;
    		case "JULY" : return Month.JULY;
    		case "AUGUST" : return Month.AUGUST;
    		case "SEPTEMBER" : return Month.SEPTEMBER;
    		case "OCTOBER" : return Month.OCTOBER;
    		case "NOVEMBER" : return Month.NOVEMBER;
    		case "DECEMBER" : return Month.DECEMBER;
    		default: throw new IllegalArgumentException("Seen unexpected Month: " + s);
    	}
    }

    @Override
    public String toNonNullValue(Month value) {
        return value.toString();
    }
}
