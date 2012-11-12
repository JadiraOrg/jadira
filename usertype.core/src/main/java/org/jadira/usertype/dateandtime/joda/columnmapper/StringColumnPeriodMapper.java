/*
 *  Copyright 2010, 2011, 2012 Christopher Pheby
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

import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class StringColumnPeriodMapper extends AbstractStringColumnMapper<Period> {

	private static final long serialVersionUID = 178976455398902407L;

	private static final PeriodType STANDARD = PeriodType.standard();

    @Override
    public Period fromNonNullValue(String s) {
    	
		int separatorIndex = s.indexOf('{');
		if (separatorIndex == -1) {
			return new Period(s);
		} else {
			Period period = new Period(s.substring(0, separatorIndex));
			return period.withPeriodType(determinePeriodType(s.substring(separatorIndex + 1, s.length() - 1)));
		}
    }
    
	@Override
    public String toNonNullValue(Period value) {
    	
    	final String periodString; 
		if (STANDARD.equals(value.getPeriodType())) {
			periodString = value.toString();
		} else {
			if (PeriodType.class.equals(value.getPeriodType().getClass())) {
				periodString = value.toString() + "{" + value.getPeriodType().getName() + "}";
			} else {
				
				throw new IllegalArgumentException("Subclasses of PeriodType are unsupported");
			}
		}
		return periodString;
    }
	
    private PeriodType determinePeriodType(String s) {

    	PeriodType periodType = PeriodType.standard();
    	
    	String current = s;
    	
		if (current.startsWith(PeriodType.standard().getName())) {
			periodType = PeriodType.standard();
			current = s.substring(PeriodType.standard().getName().length());
			
		} else if (current.startsWith(PeriodType.yearMonthDayTime().getName())) {
			periodType = PeriodType.yearMonthDayTime();
			current = s.substring(PeriodType.yearMonthDayTime().getName().length());
			
		} else if (current.startsWith(PeriodType.yearMonthDay().getName())) {
			periodType = PeriodType.yearMonthDay();
			current = s.substring(PeriodType.yearMonthDay().getName().length());
			
		} else if (current.startsWith(PeriodType.yearWeekDayTime().getName())) {
			periodType = PeriodType.yearWeekDayTime();
			current = s.substring(PeriodType.yearWeekDayTime().getName().length());
			
		} else if (current.startsWith(PeriodType.yearWeekDay().getName())) {
			periodType = PeriodType.yearWeekDay();
			current = s.substring(PeriodType.yearWeekDay().getName().length());
			
		} else if (current.startsWith(PeriodType.yearDayTime().getName())) {
			periodType = PeriodType.yearDayTime();
			current = s.substring(PeriodType.yearDayTime().getName().length());
			
		} else if (current.startsWith(PeriodType.yearDay().getName())) {
			periodType = PeriodType.yearDay();
			current = s.substring(PeriodType.yearDay().getName().length());
			
		} else if (current.startsWith(PeriodType.dayTime().getName())) {
			periodType = PeriodType.dayTime();
			current = s.substring(PeriodType.dayTime().getName().length());
			
		} else if (current.startsWith(PeriodType.time().getName())) {
			periodType = PeriodType.time();
			current = s.substring(PeriodType.time().getName().length());
			
		} else if (current.startsWith(PeriodType.years().getName())) {
			periodType = PeriodType.years();
			current = s.substring(PeriodType.years().getName().length());
			
		} else if (current.startsWith(PeriodType.months().getName())) {
			periodType = PeriodType.months();
			current = s.substring(PeriodType.months().getName().length());
			
		} else if (current.startsWith(PeriodType.weeks().getName())) {
			periodType = PeriodType.weeks();
			current = s.substring(PeriodType.weeks().getName().length());
			
		} else if (current.startsWith(PeriodType.days().getName())) {
			periodType = PeriodType.days();
			current = s.substring(PeriodType.days().getName().length());
			
		} else if (current.startsWith(PeriodType.hours().getName())) {
			periodType = PeriodType.hours();
			current = s.substring(PeriodType.hours().getName().length());
			
		} else if (current.startsWith(PeriodType.minutes().getName())) {
			periodType = PeriodType.minutes();
			current = s.substring(PeriodType.minutes().getName().length());
			
		} else if (current.startsWith(PeriodType.seconds().getName())) {
			periodType = PeriodType.seconds();
			current = s.substring(PeriodType.seconds().getName().length());
			
		} else if (current.startsWith(PeriodType.millis().getName())) {
			periodType = PeriodType.millis();
			current = s.substring(PeriodType.millis().getName().length());		
		}
		
    	while(current.length() > 0) {

    		if (current.startsWith("NoYears")) {
    			periodType = periodType.withYearsRemoved();
    			current = s.substring("NoYears".length());
    		} else if (current.startsWith("NoMonths")) {
    			periodType = periodType.withMonthsRemoved();
    			current = s.substring("NoMonths".length());
    		} else if (current.startsWith("NoWeeks")) {
    			periodType = periodType.withWeeksRemoved();
    			current = s.substring("NoWeeks".length());
    		} else if (current.startsWith("NoDays")) {
    			periodType = periodType.withDaysRemoved();
    			current = s.substring("NoDays".length());
    		} else if (current.startsWith("NoHours")) {
    			periodType = periodType.withHoursRemoved();
    			current = s.substring("NoHours".length());
    		} else if (current.startsWith("NoMinutes")) {
    			periodType = periodType.withMinutesRemoved();
    			current = s.substring("NoMinutes".length());
    		} else if (current.startsWith("NoSeconds")) {
    			periodType = periodType.withSecondsRemoved();
    			current = s.substring("NoSeconds".length());
    		} else if (current.startsWith("NoMillis")) {
    			periodType = periodType.withMillisRemoved();
    			current = s.substring("NoMillis".length());
    		} else {
    			throw new IllegalArgumentException("Unrecognised PeriodType: " + s + "{" + current + "}");
    		}
    	}
    	return periodType;
	}
}