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
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor;
import org.jadira.usertype.spi.shared.descriptor.sql.DstSafeTimestampTypeDescriptor;

public class DstSafeTimestampType extends
		AbstractSingleColumnStandardBasicType<Date> implements
		VersionType<Date>, LiteralType<Date> {

	private static final long serialVersionUID = -3665273920874664942L;

	public static final DstSafeTimestampType INSTANCE = new DstSafeTimestampType();

	public DstSafeTimestampType() {
		super(DstSafeTimestampTypeDescriptor.INSTANCE,
				JdbcTimestampTypeDescriptor.INSTANCE);
	}
	
	public DstSafeTimestampType(Calendar cal) {
		super(cal == null ? DstSafeTimestampTypeDescriptor.INSTANCE : new DstSafeTimestampTypeDescriptor(cal),
				JdbcTimestampTypeDescriptor.INSTANCE);
	}

	public String getName() {
		return "dstSafeTimestamp";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), Timestamp.class.getName(),
				java.util.Date.class.getName() };
	}

	public Date next(Date current, SharedSessionContractImplementor session) {
		return seed(session);
	}

	public Date seed(SharedSessionContractImplementor session) {
		return new Timestamp(System.currentTimeMillis());
	}

	public Comparator<Date> getComparator() {
		return getJavaTypeDescriptor().getComparator();
	}

	public String objectToSQLString(Date value, Dialect dialect)
			throws Exception {
		final Timestamp ts = Timestamp.class.isInstance(value) ? (Timestamp) value
				: new Timestamp(value.getTime());
		// TODO : use JDBC date literal escape syntax? -> {d 'date-string'} in
		// yyyy-mm-dd hh:mm:ss[.f...] format
		return StringType.INSTANCE.objectToSQLString(ts.toString(), dialect);
	}

	public Date fromStringValue(String xml) throws HibernateException {
		return fromString(xml);
	}
}
