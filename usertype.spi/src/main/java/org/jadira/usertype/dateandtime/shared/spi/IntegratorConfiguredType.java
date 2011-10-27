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

import org.hibernate.SessionFactory;
import org.hibernate.integrator.spi.Integrator;

/**
 * Represents a type that can be configured by an {@link Integrator} or that take additional configuration from the {@link SessionFactory}. 
 * Typically these types are configured at runtime prior to the execution of null safe get. This is necessary because integrators are 
 * invoked following the discovery of annotated types.
 */
public interface IntegratorConfiguredType {

	/**
	 * Apply configuration for the given type
	 * @param sessionFactory The currently active session factory
	 */
	void applyConfiguration(SessionFactory sessionFactory);
}
