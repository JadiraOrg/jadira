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
package org.jadira.bindings.core.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the contents of a bindings.xml configuration file. The
 * configuration contains providers which represent implementations of a
 * facility capable of resolving converters at runtime and binding configuration
 * entries which specifically register converters for a given class
 */
public class BindingConfiguration {

	private List<Provider> providers = new ArrayList<Provider>();
	
	private List<Extension<?>> extensions = new ArrayList<Extension<?>>();

	private List<BindingConfigurationEntry> bindingEntries = new ArrayList<BindingConfigurationEntry>();

	/**
	 * Return the registered providers
	 * @return List of {@link Provider}
	 */
	public List<Provider> getProviders() {
		return Collections.unmodifiableList(providers);
	}

	/**
	 * Registers a new provider
	 * @param provider The new {@link Provider}
	 */
	public void addProvider(Provider provider) {
		providers.add(provider);
	}

	/**
	 * Return the registered extensions
	 * @return List of {@link Extension}
	 */
	public List<Extension<?>> getExtensions() {
		return Collections.unmodifiableList(extensions);
	}
	
	/**
	 * Registers a new extension
	 * @param extension The new {@link Extension}
	 */
	public void addExtension(Extension<?> extension) {
		extensions.add(extension);
	}

	/**
	 * Return the registered {@link BindingConfigurationEntry} instances
	 * @return List of {@link BindingConfigurationEntry}
	 */
	public List<BindingConfigurationEntry> getBindingEntries() {
		return Collections.unmodifiableList(bindingEntries);
	}

	/**
	 * Registers a new binding entry
	 * @param bindingEntry The new {@link BindingConfigurationEntry}
	 */
	public void addBindingEntry(BindingConfigurationEntry bindingEntry) {
		bindingEntries.add(bindingEntry);
	}
}
