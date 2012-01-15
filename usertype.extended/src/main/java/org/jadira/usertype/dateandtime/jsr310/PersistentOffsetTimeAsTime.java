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
package org.jadira.usertype.dateandtime.jsr310;

import java.sql.Time;
import java.util.Properties;

import javax.time.calendar.OffsetTime;
import javax.time.calendar.ZoneOffset;

import org.hibernate.SessionFactory;
import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.jsr310.columnmapper.TimeColumnOffsetTimeMapper;
import org.jadira.usertype.spi.shared.AbstractSingleColumnUserType;
import org.jadira.usertype.spi.shared.ConfigurationHelper;
import org.jadira.usertype.spi.shared.IntegratorConfiguredType;

/**
 * Persist {@link OffsetTime} via Hibernate. This uses java.sql.Time and the time datatype of your database. You
 * will not retain millis / nanoseconds part.
 * The type is stored using UTC timezone and presented in the
 * JVM using the JVM's default zone.
 *
 * Alternatively provide the 'databaseZone' parameter in the {@link javax.time.calendar.TimeZone#forID(String)} format
 * to indicate the zone of the database. The 'javaZone' can be used to similarly configure the zone of the
 * value on return from the database.
 * N.B. To use the zone of the JVM supply 'jvm'
 */
public class PersistentOffsetTimeAsTime extends AbstractSingleColumnUserType<OffsetTime, Time, TimeColumnOffsetTimeMapper> implements ParameterizedType, IntegratorConfiguredType {

    private static final long serialVersionUID = 5138742305537333265L;
    
    private Properties parameterValues;
    
    @Override
    public void setParameterValues(Properties parameters) {
    	this.parameterValues = parameters;
    }
    
	@Override
	public void applyConfiguration(SessionFactory sessionFactory) {

		TimeColumnOffsetTimeMapper columnMapper = (TimeColumnOffsetTimeMapper) getColumnMapper();

		String databaseZone = null;
		if (parameterValues != null) {
			databaseZone = parameterValues.getProperty("databaseZone");
		}
		if (databaseZone == null) {
			databaseZone = ConfigurationHelper.getProperty("databaseZone");
		}
		if (databaseZone != null) {
			if ("jvm".equals(databaseZone)) {
				columnMapper.setDatabaseZone(null);
			} else {
				columnMapper.setDatabaseZone(ZoneOffset.of(databaseZone));
			}
		}

		String javaZone = null;
		if (parameterValues != null) {
			javaZone = parameterValues.getProperty("javaZone");
		}
		if (javaZone == null) {
			javaZone = ConfigurationHelper.getProperty("javaZone");
		}

		if (javaZone != null) {
			if ("jvm".equals(javaZone)) {
				columnMapper.setJavaZone(null);
			} else {
				columnMapper.setJavaZone(ZoneOffset.of(javaZone));
			}
		}
    }
}
