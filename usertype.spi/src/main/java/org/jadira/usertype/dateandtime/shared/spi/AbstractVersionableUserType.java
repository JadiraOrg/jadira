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
package org.jadira.usertype.dateandtime.shared.spi;

import java.util.Comparator;
import java.util.Properties;

import org.hibernate.engine.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserVersionType;

@SuppressWarnings("rawtypes")
public abstract class AbstractVersionableUserType<T, J, C extends VersionableColumnMapper<T, J>> extends AbstractUserType<T, J, C> implements UserVersionType, Comparator, ParameterizedType {
    
    private Seed<J> seed;

    public void setParameterValues(Properties parameters) {
        
        if (parameters != null) {
            
            String seedName = parameters.getProperty("seed");
            if (seedName != null) {
                
                Class<Seed<J>> seedClass;
                try {
                    @SuppressWarnings("unchecked") Class<Seed<J>> mySeedClass = (Class<Seed<J>>) Class.forName(seedName);
                    seedClass = mySeedClass;
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Referenced seed class {" + seedName + "} cannot be found");
                }
                try {
                    seed = seedClass.newInstance();
                } catch (InstantiationException e) {
                    throw new IllegalStateException("Referenced seed class {" + seedName + "} cannot be instantiated");
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Referenced seed class {" + seedName + "} cannot be accessed");
                }
            }
        } else {
            seed = null;
        }
    }
    
    public abstract int compare(Object o1, Object o2);    

    public T seed(SessionImplementor session) {
        
        final VersionableColumnMapper<T,J> columnMapper = getColumnMapper();
        if (seed == null) {
            return columnMapper.fromNonNullValue(columnMapper.generateCurrentValue());
        } else {
            return columnMapper.fromNonNullValue(seed.getTimestamp(session));
        }
    }

    public T next(Object current, SessionImplementor session) {
        return seed(session);
    }
}
