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
package org.jadira.usertype.corejava.collections;

import java.io.IOException;
import java.util.Map;

import org.hibernate.HibernateException;
import org.jadira.usertype.spi.shared.AbstractMapUserType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class PersistentStringMapAsJson extends AbstractMapUserType<String, String> {

	private static final long serialVersionUID = 2579662814723772780L;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	private ObjectReader OBJECT_READER = OBJECT_MAPPER.readerFor(new TypeReference<Map<String, String>>() {});

	private ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writerFor(new TypeReference<Map<String, String>>() {});
	
	protected ObjectReader getObjectReader() {
		return OBJECT_READER;
	}
	
	protected ObjectWriter getObjectWriter() {
		return OBJECT_WRITER;
	}
	
	protected String toString(Map<String, String> map) {
		
		try {
			return OBJECT_WRITER.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			throw new HibernateException("Cannot serialize JSON object: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected Map<String, String> toMap(String input) {

		try {
			Object obj = getObjectReader().readValue(input);
			return (Map<String, String>) obj;
		} catch (JsonParseException e) {
			throw new HibernateException("Problem parsing retrieved JSON String: " + input, e);
		} catch (JsonMappingException e) {
			throw new HibernateException("Problem mapping retrieved JSON String: " + input, e);
		} catch (IOException e) {
			throw new HibernateException("Problem reading JSON String: " + input, e);
		}
	}
}
