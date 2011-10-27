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
package org.jadira.usertype.dateandtime.joda.integrator;

import org.hibernate.integrator.spi.Integrator;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.jadira.usertype.dateandtime.joda.PersistentDateMidnight;
import org.jadira.usertype.dateandtime.joda.PersistentDateTime;
import org.jadira.usertype.dateandtime.joda.PersistentDurationAsString;
import org.jadira.usertype.dateandtime.joda.PersistentInstantAsTimestamp;
import org.jadira.usertype.dateandtime.joda.PersistentInterval;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDate;
import org.jadira.usertype.dateandtime.joda.PersistentLocalTime;
import org.jadira.usertype.dateandtime.joda.PersistentPeriodAsString;
import org.jadira.usertype.dateandtime.joda.PersistentYears;
import org.jadira.usertype.dateandtime.shared.spi.AbstractUserTypeHibernateIntegrator;

public class UserTypeJodaHibernateIntegrator extends AbstractUserTypeHibernateIntegrator implements Integrator {

	@SuppressWarnings("deprecation")
	private static final UserType[] USER_TYPES = new UserType[]{
		new PersistentDateTime(),
		new PersistentDurationAsString(),
		new PersistentInstantAsTimestamp(),
		new PersistentLocalDate(),
		new PersistentLocalTime(),
		new PersistentPeriodAsString(),
		new org.jadira.usertype.dateandtime.joda.PersistentTimeOfDay(),
		new org.jadira.usertype.dateandtime.joda.PersistentYearMonthDay(),
		new PersistentYears(),
	};

	private static final CompositeUserType[] COMPOSITE_USER_TYPES = new CompositeUserType[]{
		new PersistentDateMidnight(),
		new PersistentInterval(),
	};
	
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
