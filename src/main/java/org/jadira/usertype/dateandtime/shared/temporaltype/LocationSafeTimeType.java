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
package org.jadira.usertype.dateandtime.shared.temporaltype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.HibernateException;

public class LocationSafeTimeType extends AbstractLocationSafeUserType {

    private static final long serialVersionUID = 4610718111031354395L;

    private static final String TIME_FORMAT = "HH:mm:ss";
    
    @Override
    public java.sql.Time deepCopyNotNull(Object value) {
        return new java.sql.Time(((Date) value).getTime());
    }

    @Override
    public java.sql.Time get(ResultSet rs, String name) throws SQLException {
        return rs.getTime(name);
    }

    @Override
    public void set(PreparedStatement st, Object value, int index) throws SQLException {
        if (!(value instanceof Time)) {                
            value = deepCopy(value);
        }
        st.setTime(index, (Time) value);
    }

    @Override
    public Class<Time> returnedClass() {
        return Time.class;
    }

    @Override
    public int sqlType() {
        return Types.TIME;
    }
    
    @Override
    public Time fromStringValue(String xml) throws HibernateException {
        try {
            return new Time(new SimpleDateFormat(TIME_FORMAT).parse(xml).getTime());
        }
        catch (ParseException ex) {
            throw new HibernateException("could not parse XML", ex);
        }
    }
    
    @Override
    public String toString(Object value) throws HibernateException {
        return '\'' + new Time( ( (java.util.Date) value ).getTime() ).toString() + '\'';
    }

    public String getName() {
        return "locationSafeTime";
    }
}
 