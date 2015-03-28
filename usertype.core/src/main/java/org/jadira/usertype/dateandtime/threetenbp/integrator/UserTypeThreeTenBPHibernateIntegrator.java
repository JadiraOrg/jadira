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
package org.jadira.usertype.dateandtime.threetenbp.integrator;

import org.hibernate.integrator.spi.Integrator;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.jadira.usertype.dateandtime.threetenbp.PersistentDurationAsString;
import org.jadira.usertype.dateandtime.threetenbp.PersistentInstantAsTimestamp;
import org.jadira.usertype.dateandtime.threetenbp.PersistentLocalDate;
import org.jadira.usertype.dateandtime.threetenbp.PersistentLocalDateTime;
import org.jadira.usertype.dateandtime.threetenbp.PersistentLocalTime;
import org.jadira.usertype.dateandtime.threetenbp.PersistentMonthDayAsString;
import org.jadira.usertype.dateandtime.threetenbp.PersistentOffsetDateTime;
import org.jadira.usertype.dateandtime.threetenbp.PersistentOffsetTimeAsTimestamp;
import org.jadira.usertype.dateandtime.threetenbp.PersistentPeriodAsString;
import org.jadira.usertype.dateandtime.threetenbp.PersistentYearAsInteger;
import org.jadira.usertype.dateandtime.threetenbp.PersistentYearMonthAsString;
import org.jadira.usertype.dateandtime.threetenbp.PersistentZoneIdAsString;
import org.jadira.usertype.dateandtime.threetenbp.PersistentZoneOffsetAsString;
import org.jadira.usertype.dateandtime.threetenbp.PersistentZonedDateTime;
import org.jadira.usertype.spi.shared.AbstractUserTypeHibernateIntegrator;
import org.jadira.usertype.spi.utils.reflection.ClassLoaderUtils;

public class UserTypeThreeTenBPHibernateIntegrator extends AbstractUserTypeHibernateIntegrator implements Integrator {

	private static UserType[] userTypes;
	
	static {
		
		boolean found = false;

		try {
			Class.forName("org.threeten.bp.LocalTime", false, ClassLoaderUtils.getClassLoader());
			found = true;
		} catch (ClassNotFoundException e) {
		}

		try {
			Class.forName("org.threeten.bp.LocalTime");
			found = true;
		} catch (ClassNotFoundException e) {
		}
		
		if (found) {
	
			userTypes = new UserType[]{
				new PersistentDurationAsString(),
				new PersistentInstantAsTimestamp(),
				new PersistentLocalDate(),
				new PersistentLocalDateTime(), 
				new PersistentLocalTime(), 
				new PersistentMonthDayAsString(),
				new PersistentOffsetDateTime(), 
				new PersistentOffsetTimeAsTimestamp(), 
				new PersistentPeriodAsString(), 
				new PersistentZoneIdAsString(), 
				new PersistentYearAsInteger(), 
				new PersistentYearMonthAsString(), 
				new PersistentZonedDateTime(), 
				new PersistentZoneOffsetAsString()
			};
			
		} else {
			userTypes = new UserType[]{};		
		}
	}

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
		return userTypes;
	}	
}
