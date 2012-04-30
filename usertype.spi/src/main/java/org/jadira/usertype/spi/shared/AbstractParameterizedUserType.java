/*
 *  Copyright 2010, 2011, 2012 Christopher Pheby
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

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.usertype.ParameterizedType;

public abstract class AbstractParameterizedUserType<T, J, C extends ColumnMapper<T, J>> extends AbstractSingleColumnUserType<T, J, C> implements ParameterizedType, IntegratorConfiguredType {

	private static final long serialVersionUID = -8038898451426631564L;

	private Properties parameterValues;
    
    @Override
    public void setParameterValues(Properties parameters) {
    	this.parameterValues = parameters;
    }
    
    protected Properties getParameterValues() {
    	return parameterValues;
    }

    @Override
	public void applyConfiguration(SessionFactory sessionFactory) {
    }
}
