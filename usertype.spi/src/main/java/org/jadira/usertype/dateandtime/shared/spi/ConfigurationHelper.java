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
package org.jadira.usertype.dateandtime.shared.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.SessionFactory;

public class ConfigurationHelper {

	private static ThreadLocal<SessionFactory> currentSessionFactory = new ThreadLocal<SessionFactory>();
	
	private static final Map<SessionFactory, Properties> defaultProperties = new HashMap<SessionFactory, Properties>();
	
    private ConfigurationHelper() {
    }
    
    public static String getProperty(String key) {
    	
    	SessionFactory current = currentSessionFactory.get();
    	if (current != null) {
    		Properties defaults = defaultProperties.get(current);
    		if (defaults != null) {
    			return defaults.getProperty(key);
    		}
    	}
    	return null;
    }

	static void setCurrentSessionFactory(SessionFactory sessionFactory) {
		currentSessionFactory.set(sessionFactory);
	}
	
	static void configureDefaultProperties(SessionFactory sessionFactory, Properties properties) {
		if (properties == null) {
			defaultProperties.remove(sessionFactory);
		} else {
			defaultProperties.put(sessionFactory, properties);
		}
	}
}
