/*
 *  Copyright 2012 Chris Pheby
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
package org.jadira.scanner.file.locator;

import java.net.URL;
import java.util.List;

import org.jadira.scanner.core.api.Locator;

/**
 * A Locator that returns an explicit set of classpaths
 */
public class IdentityUrlLocator implements Locator<URL> {

	private List<URL> urls;

	public IdentityUrlLocator(List<URL> urls) {
		this.urls = urls;
	}

	@Override
	public List<URL> locate() {
		return urls;
	}
}

