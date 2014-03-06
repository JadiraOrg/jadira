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
package org.jadira.usertype.spi.shared.descriptor.sql;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.DateTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class DstSafeDateTypeDescriptor extends DateTypeDescriptor {

	private static final long serialVersionUID = -1927559005967709998L;

	private final Calendar cal; // calendar.getInstance(TimeZone.getTimeZone("GMT"));
	
	public static final DstSafeDateTypeDescriptor INSTANCE = new DstSafeDateTypeDescriptor();

	public DstSafeDateTypeDescriptor() {
		cal = null;
	}
	
	public DstSafeDateTypeDescriptor(Calendar cal) {
		this.cal = cal;
	}
	
	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {

		return new BasicBinder<X>(javaTypeDescriptor, (SqlTypeDescriptor) this) {
			@Override
			protected void doBind(PreparedStatement st, X value, int index,
					WrapperOptions options) throws SQLException {
				if (cal == null) {
					st.setDate(index,
							javaTypeDescriptor.unwrap(value, Date.class, options));
				} else {
					st.setDate(index,
							javaTypeDescriptor.unwrap(value, Date.class, options), cal);	
				}
			}
		};
	}
	
	public <X> ValueExtractor<X> getExtractor(
			final JavaTypeDescriptor<X> javaTypeDescriptor) {
		return new BasicExtractor<X>(javaTypeDescriptor, (SqlTypeDescriptor) this) {
			@Override
			protected X doExtract(ResultSet rs, String name,
					WrapperOptions options) throws SQLException {
				if (cal == null) {
					return javaTypeDescriptor.wrap(rs.getDate(name), options);
				} else {
					return javaTypeDescriptor.wrap(rs.getDate(name, cal), options);	
				}
			}
			
			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
				if (cal == null) {
					return javaTypeDescriptor.wrap(statement.getDate(index), options);
				} else {
					return javaTypeDescriptor.wrap(statement.getDate(index, cal), options);	
				}
			}

			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
				if (cal == null) {
					return javaTypeDescriptor.wrap(statement.getDate(name), options);
				} else {
					return javaTypeDescriptor.wrap(statement.getDate(name, cal), options);	
				}
			}
		};
	}
}
