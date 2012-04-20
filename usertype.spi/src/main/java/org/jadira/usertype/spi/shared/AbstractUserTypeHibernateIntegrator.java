/*
 *  Copyright 2011 Christopher Pheby
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

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

public abstract class AbstractUserTypeHibernateIntegrator implements Integrator {

	private static final String REGISTER_USERTYPES_KEY = "jadira.usertype.autoRegisterUserTypes";

	private static final String DEFAULT_JAVAZONE_KEY = "jadira.usertype.javaZone";	
	private static final String DEFAULT_DATABASEZONE_KEY = "jadira.usertype.databaseZone";
	private static final String DEFAULT_SEED_KEY = "jadira.usertype.seed";
	private static final String DEFAULT_CURRENCYCODE_KEY = "jadira.usertype.currencyCode";
	
	public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		
		ConfigurationHelper.setCurrentSessionFactory(sessionFactory);
		
		String isEnabled = configuration.getProperty(REGISTER_USERTYPES_KEY); 
		String javaZone = configuration.getProperty(DEFAULT_JAVAZONE_KEY);
		String databaseZone = configuration.getProperty(DEFAULT_DATABASEZONE_KEY);
		String seed = configuration.getProperty(DEFAULT_SEED_KEY);
		String currencyCode = configuration.getProperty(DEFAULT_CURRENCYCODE_KEY);
		configureDefaultProperties(sessionFactory, javaZone, databaseZone, seed, currencyCode);
		
		if (isEnabled != null && Boolean.valueOf(isEnabled)) {
			autoRegisterUsertypes(configuration, isEnabled);
		}
	}

	private void autoRegisterUsertypes(Configuration configuration, String isEnabled) {
		
		for(UserType next : getUserTypes()) {

			registerType(configuration, next);
		}
		
		for(CompositeUserType next : getCompositeUserTypes()) {

			registerType(configuration, next);
		}
	}

	private void configureDefaultProperties(SessionFactoryImplementor sessionFactory, String javaZone, String databaseZone, String seed, String currencyCode) {
		Properties properties = new Properties();
		if (databaseZone != null) { properties.put("databaseZone", databaseZone); }
		if (javaZone != null) { properties.put("javaZone", javaZone); }
		if (seed != null) { properties.put("seed", seed); }
		if (currencyCode != null) { properties.put("currencyCode", currencyCode); }
		ConfigurationHelper.configureDefaultProperties(sessionFactory, properties);
	}

	private void registerType(Configuration configuration, CompositeUserType type) {
		String className = type.returnedClass().getName();
		configuration.registerTypeOverride(type, new String[] {className});
	}
	
	private void registerType(Configuration configuration, UserType type) {
		String className = type.returnedClass().getName();
		configuration.registerTypeOverride(type, new String[] {className});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		// no-op
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		ConfigurationHelper.configureDefaultProperties(sessionFactory, null);
	}
	
	protected abstract CompositeUserType[] getCompositeUserTypes();
	
	protected abstract UserType[] getUserTypes();
}
