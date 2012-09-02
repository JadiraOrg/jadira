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

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A SAX entity resolving capable of resolving files from the classpath when
 * prefixed with the custom 'classpath:/' URL scheme
 */
public class BindingXmlEntityResolver implements EntityResolver {

	private static final String CLASSPATH_SCHEME = "classpath:/";

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {

		if (systemId.startsWith(CLASSPATH_SCHEME)) {
			String filename = systemId.substring(CLASSPATH_SCHEME.length());
			InputStream stream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(filename);
			return new InputSource(stream);
		} else {
			return null;
		}
	}
}
