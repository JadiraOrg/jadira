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
import org.hibernate.type.LongType;
import org.jadira.usertype.dateandtime.shared.reflectionutils.Hibernate36Helper;

public abstract class AbstractLongColumnMapper<T> extends AbstractColumnMapper<T, Long> {

    private static final long serialVersionUID = 6892220362704239520L;

    public final int getSqlType() {
        return Types.BIGINT;
    }

    public final LongType getHibernateType() {
        if (Hibernate36Helper.isHibernate36ApiAvailable()) {
            return (LongType) Hibernate36Helper.getHibernateType("LONG");
        } else {
            return (LongType) Hibernate.LONG;
        }
    }

    public abstract T fromNonNullValue(Long value);

    public abstract T fromNonNullString(String s);

    public abstract Long toNonNullValue(T value);

    public abstract String toNonNullString(T value);
}
