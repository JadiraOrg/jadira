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

import java.util.HashMap;
import java.util.Map;

import org.jadira.usertype.spi.shared.AbstractMapUserType;

public class PersistentStringMap extends AbstractMapUserType<String, String> {

	private static final long serialVersionUID = 8279418608033058332L;

	protected String toString(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			
			String key = entry.getKey();
			String value = entry.getValue();
			
			key = key.replaceAll("&(?!amp;)", "&amp;");
			value = value.replaceAll("&(?!amp;)", "&amp;");
			
			key = key.replaceAll("=", "&equals;");
			value = value.replaceAll("=", "&equals;");

			key = key.replaceAll(",", "&comma;");
			value = value.replaceAll(",", "&comma;");
			
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(key);
			
			if (value != null) {
			  sb.append("=").append(value);
			}
		}

		return sb.toString();
	}

	protected Map<String, String> toMap(String input) {

		Map<String, String> map = new HashMap<String, String>();
		
		for (String next : input.split("\n")) {
			int i = next.indexOf("=");
			
			String key;
			String value;
			
			if (i == -1) {
				key = next;
				value = null;
			} else {
				key = next.substring(0, i);
				
				value = next.substring(i + 1);
				
				value = value.replaceAll("[&]comma[;]", ",");
				value = value.replaceAll("[&]equals[;]", "=");
				value = value.replaceAll("[&]amp[;]", "&");
			}
			
			key = key.replaceAll("[&]comma[;]", ",");
			key = key.replaceAll("[&]equals[;]", "=");
			key = key.replaceAll("[&]amp[;]", "&");
			
			map.put(key, value);
		}
		return map;
	}
}
