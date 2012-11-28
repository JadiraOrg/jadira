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
package org.jadira.cdt.phonenumber.api;

import org.jadira.cdt.country.CountryCode;

/**
 * Applies to representations of phone numbers.
 */
public interface PhoneNumber {

	/**
	 * Returns the dialling code for the country that this phone number belongs to.
	 * @return The country dialling code e.g. '+44'
	 */
    String getCountryDiallingCode();
    
	/**
	 * Returns the national number part for the phone number.
	 * @return The country dialling code e.g. '1963350474'
	 */    
    String getNationalNumber();

	/**
	 * Returns the extension for the phone number or null.
	 * @return The extension
	 */
    String getExtension();

	/**
	 * Returns the E164 formatted telephone number. Any extension is ommitted.
	 * @return The telephone number formatted as E164.
	 */
    String toE164NumberString();

	/**
	 * Returns the E164 formatted telephone number. Any extension is appended to the number
	 * with the extension prefix given as ';ext='
	 * @return The telephone number formatted as E164 together with the extension, if any.
	 */
    String toE164NumberWithExtensionString();

    /**
     * Returns the CountryCode this number belongs to
     * @return The CountryCode
     */
    CountryCode extractCountryCode();
    
    /**
     * Splits the national number into its constituent parts, returning that part of the number that
     * represents the area code.
     * @return The area code
     */
    String extractAreaCode();

    /**
     * Splits the national number into its constituent parts, returning that part of the number that
     * represents the subscriber number.
     * @return The subscriber number
     */
    String extractSubscriberNumber();
}
