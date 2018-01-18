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

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.JdbcTimeTypeDescriptor;
import org.jadira.usertype.spi.shared.descriptor.sql.DstSafeTimeTypeDescriptor;

/**
 * @deprecated This class should no longer be used now that Hibernate allows configuration of  hibernate.jdbc.time_zone configuration property 
 */
@Deprecated
public class DstSafeTimeType extends
		AbstractSingleColumnStandardBasicType<Date> implements
		LiteralType<Date> {

	private static final long serialVersionUID = -3665273920874664942L;

	public static final DstSafeTimeType INSTANCE = new DstSafeTimeType();

	public DstSafeTimeType() {
		super(DstSafeTimeTypeDescriptor.INSTANCE,
				JdbcTimeTypeDescriptor.INSTANCE);
	}
	
	public DstSafeTimeType(Calendar cal) {
		super(cal == null ? DstSafeTimeTypeDescriptor.INSTANCE : new DstSafeTimeTypeDescriptor(cal),
				JdbcTimeTypeDescriptor.INSTANCE);
	}

	public String getName() {
		return "dstSafeTime";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), java.sql.Time.class.getName() };
	}

	public String objectToSQLString(Date value, Dialect dialect)
			throws Exception {
		final Time ts = Time.class.isInstance(value) ? (Time) value : new Time(
				value.getTime());
		// TODO : use JDBC date literal escape syntax? -> {d 'date-string'} in
		// yyyy-mm-dd hh:mm:ss[.f...] format
		return StringType.INSTANCE.objectToSQLString(ts.toString(), dialect);
	}
}
