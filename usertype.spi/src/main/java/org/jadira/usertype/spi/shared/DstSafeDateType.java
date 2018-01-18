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

import java.util.Calendar;
import java.util.Date;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.IdentifierType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.JdbcDateTypeDescriptor;
import org.jadira.usertype.spi.shared.descriptor.sql.DstSafeDateTypeDescriptor;

/**
 * @deprecated This class should no longer be used now that Hibernate allows configuration of  hibernate.jdbc.time_zone configuration property 
 */
@Deprecated
public class DstSafeDateType
		extends AbstractSingleColumnStandardBasicType<Date>
		implements IdentifierType<Date>, LiteralType<Date> {

	private static final long serialVersionUID = 669738618020424223L;

	public static final DstSafeDateType INSTANCE = new DstSafeDateType();

	public DstSafeDateType() {
		super(DstSafeDateTypeDescriptor.INSTANCE,
				JdbcDateTypeDescriptor.INSTANCE);
	}
	
	public DstSafeDateType(Calendar cal) {
		super(cal == null ? DstSafeDateTypeDescriptor.INSTANCE : new DstSafeDateTypeDescriptor(cal),
				JdbcDateTypeDescriptor.INSTANCE);
	}
	public String getName() {
		return "dstSafeDate";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] {
				getName(),
				java.sql.Date.class.getName()
		};
	}

	public String objectToSQLString(Date value, Dialect dialect) throws Exception {
		final java.sql.Date jdbcDate = java.sql.Date.class.isInstance( value )
				? ( java.sql.Date ) value
				: new java.sql.Date( value.getTime() );
		// TODO : use JDBC date literal escape syntax? -> {d 'date-string'} in yyyy-mm-dd format
		return StringType.INSTANCE.objectToSQLString( jdbcDate.toString(), dialect );
	}

	public Date stringToObject(String xml) {
		return fromString( xml );
	}
}
