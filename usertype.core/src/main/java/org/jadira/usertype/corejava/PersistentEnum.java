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
package org.jadira.usertype.corejava;

import java.lang.reflect.Method;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.spi.shared.AbstractReflectionUserType;

public class PersistentEnum extends AbstractReflectionUserType<Enum<?>> implements ParameterizedType {

	private static final long serialVersionUID = 3094384329334123541L;
	
	private static final String DEFAULT_VALUE_OF_METHOD_NAME = "valueOf";		
	private static final String DEFAULT_IDENTIFIER_METHOD_NAME = "name";
	
	@SuppressWarnings({ "unchecked" })
	public void setParameterValues(Properties parameters) {

		if (parameters.containsKey("enumClass")) {
			String enumClassName = parameters.getProperty("enumClass");
			try {
				setMappedClass((Class<Enum<?>>) Class.forName(enumClassName));
			} catch (ClassNotFoundException e) {
				throw new HibernateException("Specified Enum class could not be found", e);
			}
		}
		
		final Method identifierMethod;
		String identifierMethodName = parameters.getProperty("identifierMethod", DEFAULT_IDENTIFIER_METHOD_NAME);
		try {
			identifierMethod = getMappedClass().getMethod(identifierMethodName, new Class[0]);
		} catch (NoSuchMethodException e) {
			throw new HibernateException("Specified identifier method could not be found", e);
		}
		setIdentifierMethod(identifierMethod);
			
		String valueOfMethodName = parameters.getProperty("valueOfMethod", DEFAULT_VALUE_OF_METHOD_NAME);

		try {
			setValueOfMethod(getMappedClass().getMethod(valueOfMethodName, new Class[] { identifierMethod.getReturnType() }));
		} catch (NoSuchMethodException e) {
			throw new HibernateException("Specified valueOf method could not be found", e);
		}
				
		super.setParameterValues(parameters);
	}
}
