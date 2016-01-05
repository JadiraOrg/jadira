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

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.usertype.EnhancedUserType;
import org.jadira.usertype.spi.utils.reflection.TypeHelper;

public abstract class AbstractKnownClassHeuristicUserType<T> extends AbstractHeuristicUserType implements EnhancedUserType, Serializable {

	private static final long serialVersionUID = 2233911693851349367L;

	private Class<T> mappedClass;

	protected void setMappedClass(Class<T> mappedClass) {
    	this.mappedClass = mappedClass;
    }
    
    protected Class<T> getMappedClass() {
    	return mappedClass;
    }
        
	public void setParameterValues(Properties parameters) {

		if (mappedClass == null) {
			
			throw new IllegalStateException("No mapped class was defined for " + this.getClass().getName());
		}		
		
		super.setParameterValues(parameters);
	}

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> returnedClass() {
        return (Class<T>) TypeHelper.getTypeArguments(AbstractKnownClassHeuristicUserType.class, getClass()).get(0);
    }   
}
