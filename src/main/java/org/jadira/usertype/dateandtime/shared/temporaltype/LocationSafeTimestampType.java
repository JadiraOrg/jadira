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
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.HibernateException;

public class LocationSafeTimestampType extends AbstractLocationSafeUserType {

    private static final long serialVersionUID = -6374256465793823865L;

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    @Override
    public Timestamp deepCopyNotNull(Object value) {
        return new Timestamp(((Date) value).getTime());
    }

    @Override
    public Timestamp get(ResultSet rs, String name) throws SQLException {
        return rs.getTimestamp(name, getUtcCalendar());
    }

    @Override
    public void set(PreparedStatement st, Object value, int index) throws SQLException {
        if (!(value instanceof Timestamp))                
            value = deepCopy(value);
        st.setTimestamp(index, (Timestamp) value, getUtcCalendar());
    }

    @Override
    public Class<Timestamp> returnedClass() {
        return Timestamp.class;
    }

    @Override
    public int sqlType() {
        return Types.TIMESTAMP;
    }
    
    @Override
    public Timestamp fromStringValue(String xml) throws HibernateException {
        try {
            return new Timestamp( new SimpleDateFormat(TIMESTAMP_FORMAT).parse(xml).getTime() );
        }
        catch (ParseException pe) {
            throw new HibernateException("could not parse XML", pe);
        }
    }
    
    @Override
    public String toString(Object value) throws HibernateException {
        return '\'' + new Timestamp( ( (java.util.Date) value ).getTime() ).toString() + '\'';
    }

    public String getName() {
        return "locationSafeTimestamp";
    }
}
 