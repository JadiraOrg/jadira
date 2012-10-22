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
package org.jadira.usertype.dateandtime.joda;

import org.hibernate.SessionFactory;
import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.joda.columnmapper.TimestampColumnDateTimeMapper;
import org.jadira.usertype.spi.reflectionutils.ArrayUtils;
import org.jadira.usertype.spi.shared.AbstractParameterizedMultiColumnUserType;
import org.jadira.usertype.spi.shared.ConfigurationHelper;
import org.jadira.usertype.spi.shared.IntegratorConfiguredType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

/**
 * Persist {@link Interval} via Hibernate. The interval type is intended to be compatible with
 * {@link org.joda.time.contrib.hibernate.PersistentInterval} and stores beginning and end values
 * using the UTC zone and presented in the
 * JVM using the JVM's default zone.
 *
 * Alternatively provide the 'databaseZone' parameter in the {@link DateTimeZone#forID(String)} format
 * to indicate the zone of the database. The 'javaZone' can be used to similarly configure the zone of the
 * value on return from the database.
 * N.B. To use the zone of the JVM supply 'jvm'
 */
public class PersistentInterval extends AbstractParameterizedMultiColumnUserType<Interval> implements ParameterizedType, IntegratorConfiguredType {

    private static final long serialVersionUID = 1364221029392346011L;

    private TimestampColumnDateTimeMapper[] columnMappers;

    private static final String[] propertyNames = new String[]{ "begin", "end" };
    
    @Override
    protected void initialise() {
    	columnMappers = new TimestampColumnDateTimeMapper[] { new TimestampColumnDateTimeMapper(), new TimestampColumnDateTimeMapper() };
    	super.initialise();
    }
    
    @Override
    protected Interval fromConvertedColumns(Object[] convertedColumns) {

        DateTime begin = (DateTime) convertedColumns[0];
        DateTime end = (DateTime) convertedColumns[1];

        return new Interval(begin, end);
    }

    @Override
    protected TimestampColumnDateTimeMapper[] getColumnMappers() {
        return columnMappers;
    }

    @Override
    protected Object[] toConvertedColumns(Interval value) {

        return new Object[] { value.getStart(), value.getEnd() };
    }

    @Override
    public String[] getPropertyNames() {

        return ArrayUtils.copyOf(propertyNames);
    }
    

	@Override
	public void applyConfiguration(SessionFactory sessionFactory) {
		
		super.applyConfiguration(sessionFactory);

		for (int i = 0; i < getColumnMappers().length; i++) {
			
			TimestampColumnDateTimeMapper columnMapper = columnMappers[i];
	
	        String databaseZone = null;
	        if (getParameterValues() != null) {
	        	databaseZone = getParameterValues().getProperty("databaseZone");
	        }
			if (databaseZone == null) {
				databaseZone = ConfigurationHelper.getProperty("databaseZone");
			}
			
	        if (databaseZone != null) {
	            if ("jvm".equals(databaseZone)) {
	                columnMapper.setDatabaseZone(null);
	            } else {
	                columnMapper.setDatabaseZone(DateTimeZone.forID(databaseZone));
	            }
	        }
	        
	        String javaZone = null;
	        if (getParameterValues() != null) {
	        	javaZone = getParameterValues().getProperty("javaZone");
	        }
			if (javaZone == null) {
				javaZone = ConfigurationHelper.getProperty("javaZone");
			}
			
	        if (javaZone != null) {
	            if ("jvm".equals(javaZone)) {
	                columnMapper.setJavaZone(null);
	            } else {
	                columnMapper.setJavaZone(DateTimeZone.forID(javaZone));
	            }
	        }
		}
	}
}
