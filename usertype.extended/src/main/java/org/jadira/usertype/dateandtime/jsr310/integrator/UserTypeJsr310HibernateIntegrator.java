/*
 *  Copyright 2011 Christopher Pheby
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
package org.jadira.usertype.dateandtime.jsr310.integrator;

import org.hibernate.integrator.spi.Integrator;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.jadira.usertype.dateandtime.jsr310.PersistentDurationAsString;
import org.jadira.usertype.dateandtime.jsr310.PersistentInstantAsTimestamp;
import org.jadira.usertype.dateandtime.jsr310.PersistentLocalDate;
import org.jadira.usertype.dateandtime.jsr310.PersistentLocalDateTime;
import org.jadira.usertype.dateandtime.jsr310.PersistentLocalTime;
import org.jadira.usertype.dateandtime.jsr310.PersistentMonthDayAsString;
import org.jadira.usertype.dateandtime.jsr310.PersistentOffsetDate;
import org.jadira.usertype.dateandtime.jsr310.PersistentOffsetDateTime;
import org.jadira.usertype.dateandtime.jsr310.PersistentOffsetTimeAsTimestamp;
import org.jadira.usertype.dateandtime.jsr310.PersistentPeriodAsString;
import org.jadira.usertype.dateandtime.jsr310.PersistentTimeZoneAsString;
import org.jadira.usertype.dateandtime.jsr310.PersistentYear;
import org.jadira.usertype.dateandtime.jsr310.PersistentYearMonthAsString;
import org.jadira.usertype.dateandtime.jsr310.PersistentZoneOffsetAsString;
import org.jadira.usertype.dateandtime.jsr310.PersistentZonedDateTime;
import org.jadira.usertype.spi.shared.AbstractUserTypeHibernateIntegrator;

public class UserTypeJsr310HibernateIntegrator extends AbstractUserTypeHibernateIntegrator implements Integrator {

	private static final UserType[] USER_TYPES = new UserType[]{
		new PersistentDurationAsString(),
		new PersistentInstantAsTimestamp(),
		new PersistentLocalDate(),
		new PersistentLocalDateTime(), 
		new PersistentLocalTime(), 
		new PersistentMonthDayAsString(),
		new PersistentOffsetDate(), 
		new PersistentOffsetDateTime(), 
		new PersistentOffsetTimeAsTimestamp(), 
		new PersistentPeriodAsString(), 
		new PersistentTimeZoneAsString(), 
		new PersistentYear(), 
		new PersistentYearMonthAsString(), 
		new PersistentZonedDateTime(), 
		new PersistentZoneOffsetAsString()
	};

	private static final CompositeUserType[] COMPOSITE_USER_TYPES = new CompositeUserType[]{};
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CompositeUserType[] getCompositeUserTypes() {
		return COMPOSITE_USER_TYPES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UserType[] getUserTypes() {
		return USER_TYPES;
	}	
}
