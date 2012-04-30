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

import java.util.Comparator;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserVersionType;

@SuppressWarnings("rawtypes")
public abstract class AbstractVersionableUserType<T, J, C extends VersionableColumnMapper<T, J>> extends AbstractParameterizedUserType<T, J, C>
       implements UserVersionType, Comparator, ParameterizedType, IntegratorConfiguredType {

	private static final long serialVersionUID = -8127535032447082933L;

	private Seed<J> seed;
    
    @Override
	public void applyConfiguration(SessionFactory sessionFactory) {
    	
    	if (seed == null) {

    		String seedName = null;
			if (getParameterValues() != null) {
				seedName = getParameterValues().getProperty("seed");
			}
			if (seedName == null) {
				seedName = ConfigurationHelper.getProperty("seed");
			}
			if (seedName != null) {
	
				Class<Seed<J>> seedClass;
				try {
					@SuppressWarnings("unchecked")
					Class<Seed<J>> mySeedClass = (Class<Seed<J>>) Class
							.forName(seedName);
					seedClass = mySeedClass;
				} catch (ClassNotFoundException ex) {
					throw new IllegalStateException("Referenced seed class {"
							+ seedName + "} cannot be found");
				}
				try {
					seed = seedClass.newInstance();
				} catch (InstantiationException ex) {
					throw new IllegalStateException("Referenced seed class {"
							+ seedName + "} cannot be instantiated");
				} catch (IllegalAccessException ex) {
					throw new IllegalStateException("Referenced seed class {"
							+ seedName + "} cannot be accessed");
				}
			}
    	}
    }

    @Override
    public abstract int compare(Object o1, Object o2);

    @Override
    public T seed(SessionImplementor session) {

        final VersionableColumnMapper<T, J> columnMapper = getColumnMapper();
        if (seed == null) {
            return columnMapper.fromNonNullValue(columnMapper.generateCurrentValue());
        } else {
            return columnMapper.fromNonNullValue(seed.getTimestamp(session));
        }
    }

    @Override
    public T next(Object current, SessionImplementor session) {
        return seed(session);
    }
}
