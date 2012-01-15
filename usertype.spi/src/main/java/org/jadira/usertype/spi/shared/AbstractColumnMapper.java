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
package org.jadira.usertype.spi.shared;

import java.io.Serializable;

import org.jadira.usertype.spi.reflectionutils.TypeHelper;


public abstract class AbstractColumnMapper<T, J> implements Serializable, ColumnMapper<T, J> {

    private static final long serialVersionUID = 8169781475234949904L;

    /* (non-Javadoc)
     * @see org.jadira.usertype.dateandtime.shared.spi.ColumnMapper#returnedClass()
     */
    @Override
    public final Class<T> returnedClass() {
        @SuppressWarnings("unchecked")Class<T> result = (Class<T>) TypeHelper.getTypeArguments(AbstractColumnMapper.class, getClass()).get(0);
        return result;
    }

    /* (non-Javadoc)
     * @see org.jadira.usertype.dateandtime.shared.spi.ColumnMapper#jdbcClass()
     */
    @Override
    public final Class<J> jdbcClass() {
        @SuppressWarnings("unchecked")
        Class<J> result = (Class<J>) TypeHelper.getTypeArguments(ColumnMapper.class, getClass()).get(1);
        return result;
    }
}
