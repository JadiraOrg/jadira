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
import org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime;
import org.jadira.usertype.dateandtime.joda.PersistentLocalTime;
import org.jadira.usertype.dateandtime.joda.PersistentMinutes;
import org.jadira.usertype.dateandtime.joda.PersistentMonthDayAsString;
import org.jadira.usertype.dateandtime.joda.PersistentPeriodAsString;
import org.jadira.usertype.dateandtime.joda.PersistentYears;
import org.jadira.usertype.spi.shared.AbstractUserTypeHibernateIntegrator;
import org.jadira.usertype.spi.utils.reflection.ClassLoaderUtils;

@SuppressWarnings("deprecation")
public class UserTypeJodaTimeHibernateIntegrator extends AbstractUserTypeHibernateIntegrator implements Integrator {

	private static UserType[] userTypes;
	private static CompositeUserType[] compositeUserTypes;

	static {
		
		try {
			Class.forName("org.joda.time.DateTime", false, ClassLoaderUtils.getClassLoader());
			
			userTypes = new UserType[] {
				new PersistentDateTime(),
				new PersistentDurationAsString(),
				new PersistentInstantAsTimestamp(),
				new PersistentLocalDate(),
				new PersistentLocalDateTime(),
				new PersistentLocalTime(),
				new PersistentMonthDayAsString(),
				new PersistentPeriodAsString(),
				new org.jadira.usertype.dateandtime.joda.PersistentTimeOfDay(),
				new org.jadira.usertype.dateandtime.joda.PersistentYearMonthDay(),
				new PersistentYears(),
				new PersistentMinutes(),
			};
			compositeUserTypes = new CompositeUserType[] {
				new PersistentDateMidnight(),
				new PersistentInterval(),
			};
		} catch (ClassNotFoundException e) {
			userTypes = new UserType[]{};
			compositeUserTypes = new CompositeUserType[]{};
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CompositeUserType[] getCompositeUserTypes() {
		return compositeUserTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UserType[] getUserTypes() {
		return userTypes;
	}	
}
