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
package org.jadira.usertype.spi.shared;

import java.sql.Time;
import java.sql.Types;
import java.util.TimeZone;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.TimeType;
import org.jadira.usertype.spi.timezone.proxy.TimeZoneProvidingSharedSessionContractImplementor;

public abstract class AbstractTimeColumnMapper<T> extends AbstractColumnMapper<T, Time> implements DatabaseZoneConfigured {

    private static final long serialVersionUID = -3070239764121234482L;

	private TimeZone databaseZone;
    
    @Override
    public final TimeType getHibernateType() {
    	return TimeType.INSTANCE;
    }

    @Override
    public final int getSqlType() {
        return Types.TIME;
    }

    @Override
    public abstract T fromNonNullValue(Time value);

    @Override
    public abstract T fromNonNullString(String s);

    @Override
    public abstract Time toNonNullValue(T value);

    @Override
    public abstract String toNonNullString(T value);
    
	@Override
	public void setDatabaseZone(TimeZone databaseZone) {
		this.databaseZone = databaseZone;
	}

	@Override
	public TimeZone parseZone(String zoneString) {
		return TimeZone.getTimeZone(zoneString);
	}

	@Override
	public TimeZone getDatabaseZone() {
		return databaseZone;
	}

	public SharedSessionContractImplementor wrapSession(SharedSessionContractImplementor session) {
		
		final SharedSessionContractImplementor mySession;

		if (databaseZone != null) {
			mySession = new TimeZoneProvidingSharedSessionContractImplementor(session, databaseZone);
		} else {
			mySession = session;
		}
		
		return mySession;
	}
}
