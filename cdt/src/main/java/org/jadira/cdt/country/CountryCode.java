/*
 *  Copyright 2012 Christopher Pheby
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
package org.jadira.cdt.country;

/**
 * Defines a Country Code
 */
public interface CountryCode {

	/**
	 * Get the country name.
	 * @return The country name.
	 */
	String getCountryName();

	/**
	 * Get the <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2" >ISO 3166-1 alpha-2</a> code.
	 * @return The <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">ISO 3166-1 alpha-2</a> code.
	 */
	String getAlpha2Code();

	/**
	 * Get the <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3" >ISO 3166-1 alpha-3</a> code.
	 * @return The <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3">ISO 3166-1 alpha-3</a> code.
	 */
	String getAlpha3Code();

	/**
	 * Get the <a href="http://en.wikipedia.org/wiki/ISO_3166-1_numeric" >ISO 3166-1 numeric</a> code.
	 * @return The <a href="http://en.wikipedia.org/wiki/ISO_3166-1_numeric">ISO 3166-1 numeric</a> code.
	 */
	Integer getNumericCode();
	
	/**
	 * Gets the <a href="http://en.wikipedia.org/wiki/E.164">International Direct Dial</a> prefix for the country
	 * @return
	 */
	Integer getDiallingRegionCode();

}
