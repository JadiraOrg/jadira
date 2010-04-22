/*
 *  Copyright 2010 Christopher Pheby
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
package org.jadira.usertype.jsr310.temporaltype;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.hibernate.HibernateException;

public class LocationSafeDateType extends AbstractLocationSafeUserType {

    private static final long serialVersionUID = 3750449908655738619L;

    private static final String DATE_FORMAT = "dd MMMM yyyy";
    
    @Override
    public java.sql.Date deepCopyNotNull(Object value) {
        return new java.sql.Date(((java.util.Date) value).getTime());
    }

    @Override
    public Class<java.sql.Date> returnedClass() {
        return java.sql.Date.class;
    }

    @Override
    public int sqlType() {
        return Types.DATE;
    }

    @Override
    public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
        return rs.getDate(name, getUtcCalendar());
    }

    @Override
    public Object fromStringValue(String xml) throws HibernateException {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(xml);
        }
        catch (ParseException pe) {
            throw new HibernateException("could not parse XML", pe);
        }
    }
    
    @Override
    public String toString(Object value) throws HibernateException {
        return new SimpleDateFormat(DATE_FORMAT).format((java.util.Date)value);
    }

    public String getName() {
        return "locationSafeDate";
    }
}
 