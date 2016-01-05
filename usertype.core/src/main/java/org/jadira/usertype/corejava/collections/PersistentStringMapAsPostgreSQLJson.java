/*
 *  Copyright 2010, 2011, 2012 Christopher Pheby
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
package org.jadira.usertype.corejava.collections;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.postgresql.util.PGobject;

public class PersistentStringMapAsPostgreSQLJson extends PersistentStringMapAsJson {

	private static final long serialVersionUID = 849294691718991328L;

	@Override
	public Map<String, String> doNullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {

		Object identifier = rs.getObject(names[0]);

		if (rs.wasNull()) {
			return null;
		}

		final String jsonText;
		if (identifier instanceof PGobject) {
			PGobject pg = (PGobject) identifier;
			jsonText = pg.getValue();
		} else if (identifier instanceof String) { // some PostgreSQL Dialects /
													// Versions return String
													// not PGObject
			jsonText = (String) identifier;
		} else {
			throw new IllegalArgumentException("PersistentJsonObjectAsPostgreSQLJson type expected PGobject, received "
					+ identifier.getClass().getName() + " with value of '" + identifier + "'");
		}

		return toMap(jsonText);
	}

	@Override
	public void doNullSafeSet(PreparedStatement preparedStatement, Map<String, String> value, int index,
			SessionImplementor session) throws SQLException {

		if (value == null) {
			preparedStatement.setNull(index, Types.NULL);
		} else {

			String identifier = toString(value);
			;

			PGobject jsonObject = new PGobject();
			jsonObject.setType("json");
			jsonObject.setValue(identifier);

			preparedStatement.setObject(index, jsonObject);
		}
	}

}
