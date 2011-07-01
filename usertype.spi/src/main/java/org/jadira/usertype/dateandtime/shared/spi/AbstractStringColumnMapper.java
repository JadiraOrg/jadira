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
package org.jadira.usertype.dateandtime.shared.spi;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.type.StringType;
import org.jadira.usertype.dateandtime.shared.reflectionutils.Hibernate36Helper;

public abstract class AbstractStringColumnMapper<T> extends AbstractColumnMapper<T, String> {

    private static final long serialVersionUID = 790854698317453823L;

    public final int getSqlType() {
        return Types.VARCHAR;
    }

    public final StringType getHibernateType() {
        if (Hibernate36Helper.isHibernate36ApiAvailable()) {
            return (StringType) Hibernate36Helper.getHibernateType("STRING");
        } else {
            return (StringType) Hibernate.STRING;
        }
    }


    public abstract T fromNonNullValue(String s);

    public final T fromNonNullString(String s) {
        return fromNonNullValue(s);
    }

    public abstract String toNonNullValue(T value);

    public final String toNonNullString(T value) {
        return toNonNullValue(value);
    }
}
