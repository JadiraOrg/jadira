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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.jadira.usertype.spi.utils.runtime.JavaVersion;

public abstract class AbstractUserTypeHibernateIntegrator implements Integrator {

	private static final String REGISTER_USERTYPES_KEY = "jadira.usertype.autoRegisterUserTypes";

	private static final String DEFAULT_JAVAZONE_KEY = "jadira.usertype.javaZone";	
	private static final String DEFAULT_DATABASEZONE_KEY = "jadira.usertype.databaseZone";
	private static final String DEFAULT_SEED_KEY = "jadira.usertype.seed";
	private static final String DEFAULT_CURRENCYCODE_KEY = "jadira.usertype.currencyCode";
	
	private static final String JDBC42_API_KEY = "jadira.usertype.useJdbc42Apis";
	
	public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
	    
		try {
			ConfigurationHelper.setCurrentSessionFactory(sessionFactory);
		
			String isEnabled = configuration.getProperty(REGISTER_USERTYPES_KEY); 
			String javaZone = configuration.getProperty(DEFAULT_JAVAZONE_KEY);
			String databaseZone = configuration.getProperty(DEFAULT_DATABASEZONE_KEY);
			String seed = configuration.getProperty(DEFAULT_SEED_KEY);
			String currencyCode = configuration.getProperty(DEFAULT_CURRENCYCODE_KEY);
			
			String jdbc42Apis = configuration.getProperty(JDBC42_API_KEY);
			
			configureDefaultProperties(sessionFactory, javaZone, databaseZone, seed, currencyCode, jdbc42Apis);
		
			if (isEnabled != null && Boolean.valueOf(isEnabled)) {
				autoRegisterUsertypes(configuration);
			}
			
			final boolean use42Api = use42Api(configuration.getProperty(JDBC42_API_KEY), sessionFactory);
			ConfigurationHelper.setUse42Api(sessionFactory, use42Api);
			
			// doIntegrate(configuration, sessionFactory, serviceRegistry);
		} finally {
			ConfigurationHelper.setCurrentSessionFactory(null);
		}
	}

    private boolean use42Api(String jdbc42Apis, SessionFactoryImplementor sessionFactory) {
   
	    boolean use42Api;
        if (jdbc42Apis == null) {

            if (JavaVersion.isJava8OrLater()) {
             
             Connection conn = null;
             try {
                    JdbcServices jdbcServices = sessionFactory.getServiceRegistry().getService(JdbcServices.class);
                    conn = jdbcServices.getBootstrapJdbcConnectionAccess().obtainConnection();
                    
                    DatabaseMetaData dmd = conn.getMetaData();
                    int driverMajorVersion = dmd.getDriverMajorVersion();
                    int driverMinorVersion = dmd.getDriverMinorVersion();
                    
                    if (driverMajorVersion >= 5) {
                        use42Api = true;
                    } else if (driverMajorVersion >= 4 && driverMinorVersion >= 2) {
                        use42Api = true;
                    } else {
                        use42Api = false;
                    }
                } catch (SQLException e) {
                    use42Api = false;
                } catch (NoSuchMethodError e) {
                  // Occurs in Hibernate 4.2.12
                    use42Api = false;
                } finally {
                    
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            // Ignore
                        }
                    }
                }
            } else {
                use42Api = false;
            }
        } else {
            use42Api = Boolean.parseBoolean(jdbc42Apis);
        }
        return use42Api;
    }

    private void autoRegisterUsertypes(Configuration configuration) {
	
		for(UserType next : getUserTypes()) {

			registerType(configuration, next);
		}
		
		for(CompositeUserType next : getCompositeUserTypes()) {

			registerType(configuration, next);
		}
	}
    
    private void autoRegisterUsertypes(MetadataImplementor configuration) {
    	
		for(UserType next : getUserTypes()) {

			registerType(configuration, next);
		}
		
		for(CompositeUserType next : getCompositeUserTypes()) {

			registerType(configuration, next);
		}
	}

	private void configureDefaultProperties(SessionFactoryImplementor sessionFactory, String javaZone, String databaseZone, String seed, String currencyCode, String jdbc42Apis) {
		Properties properties = new Properties();
		if (databaseZone != null) { properties.put("databaseZone", databaseZone); }
		if (javaZone != null) { properties.put("javaZone", javaZone); }
		if (seed != null) { properties.put("seed", seed); }
		if (currencyCode != null) { properties.put("currencyCode", currencyCode); }
		if (jdbc42Apis != null) { properties.put("jdbc42Apis", jdbc42Apis); }
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
	
	private void registerType(MetadataImplementor mi, CompositeUserType type) {
		String className = type.returnedClass().getName();
		mi.getTypeResolver().registerTypeOverride(type, new String[] {className});
	}
	
	private void registerType(MetadataImplementor mi, UserType type) {
		String className = type.returnedClass().getName();
		mi.getTypeResolver().registerTypeOverride(type, new String[] {className});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

		final MetadataImplementor mi;
		if (metadata instanceof MetadataImplementor) {
			mi = (MetadataImplementor)metadata;
		} else {
			throw new IllegalArgumentException("Metadata was not assignable to MetadataImplementor: " + metadata.getClass());
		}
		
		try {
			ConfigurationHelper.setCurrentSessionFactory(sessionFactory);
		
			String isEnabled = (String)sessionFactory.getProperties().get(REGISTER_USERTYPES_KEY); 
			String javaZone = (String)sessionFactory.getProperties().get(DEFAULT_JAVAZONE_KEY);
			String databaseZone = (String)sessionFactory.getProperties().get(DEFAULT_DATABASEZONE_KEY);
			String seed = (String)sessionFactory.getProperties().get(DEFAULT_SEED_KEY);
			String currencyCode = (String)sessionFactory.getProperties().get(DEFAULT_CURRENCYCODE_KEY);
			
			String jdbc42Apis = (String)sessionFactory.getProperties().get(JDBC42_API_KEY);
			
			configureDefaultProperties(sessionFactory, javaZone, databaseZone, seed, currencyCode, jdbc42Apis);
		
			if (isEnabled != null && Boolean.valueOf(isEnabled)) {
				autoRegisterUsertypes(mi);
			}
			
			final boolean use42Api = use42Api(jdbc42Apis, sessionFactory);
			ConfigurationHelper.setUse42Api(sessionFactory, use42Api);
			
			// doIntegrate(mi, sessionFactory, serviceRegistry);
		} finally {
			ConfigurationHelper.setCurrentSessionFactory(null);
		}
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
