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

import org.hibernate.type.IntegerType;
import org.hibernate.type.StandardBasicTypes;

public abstract class AbstractIntegerColumnMapper<T> extends AbstractColumnMapper<T, Integer> {

    private static final long serialVersionUID = 6892220362704239520L;

    @Override
    public final int getSqlType() {
        return Types.INTEGER;
    }

    @Override
    public final IntegerType getHibernateType() {
        return StandardBasicTypes.INTEGER;
    }

    @Override
    public abstract T fromNonNullValue(Integer value);

    @Override
    public abstract T fromNonNullString(String s);

    @Override
    public abstract Integer toNonNullValue(T value);

    @Override
    public abstract String toNonNullString(T value);
}
