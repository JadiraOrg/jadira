/*
 *  Copyright 2013 Christopher Pheby
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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Comparator;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.VersionType;
import org.jadira.usertype.spi.shared.descriptor.jdbc.JdbcDateTimeTypeDescriptor;
import org.jadira.usertype.spi.shared.descriptor.sql.DstSafeTimestampTypeDescriptor;
import org.joda.time.DateTime;

public class DstSafeDateTimeType extends
		AbstractSingleColumnStandardBasicType<DateTime> implements
		VersionType<DateTime>, LiteralType<DateTime> {

	private static final long serialVersionUID = -3665273920874664942L;

	public static final DstSafeDateTimeType INSTANCE = new DstSafeDateTimeType();

	public DstSafeDateTimeType() {
		super(DstSafeTimestampTypeDescriptor.INSTANCE,
				JdbcDateTimeTypeDescriptor.INSTANCE);
	}
	
	public DstSafeDateTimeType(Calendar cal) {
		super(cal == null ? DstSafeTimestampTypeDescriptor.INSTANCE : new DstSafeTimestampTypeDescriptor(cal),
				JdbcDateTimeTypeDescriptor.INSTANCE);
	}

	public String getName() {
		return "dstSafeDateTime";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), Timestamp.class.getName(),
				java.util.Date.class.getName() };
	}

	public DateTime next(DateTime current, SessionImplementor session) {
		return seed(session);
	}

	public DateTime seed(SessionImplementor session) {
		return new DateTime();
	}

	public Comparator<DateTime> getComparator() {
		return getJavaTypeDescriptor().getComparator();
	}

	public String objectToSQLString(DateTime value, Dialect dialect)
			throws Exception {
		final Timestamp ts = new Timestamp(value.getMillis());
		// TODO : use JDBC date literal escape syntax? -> {d 'date-string'} in
		// yyyy-mm-dd hh:mm:ss[.f...] format
		return StringType.INSTANCE.objectToSQLString(ts.toString(), dialect);
	}

	public DateTime fromStringValue(String xml) throws HibernateException {
		return fromString(xml);
	}
}
