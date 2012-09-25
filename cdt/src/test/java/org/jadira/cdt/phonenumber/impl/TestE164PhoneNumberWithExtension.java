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
package org.jadira.cdt.phonenumber.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jadira.cdt.country.ISOCountryCode;
import org.junit.Test;

public class TestE164PhoneNumberWithExtension {

	@Test
	public void testDetermineISOCountryCode() {
		E164PhoneNumberWithExtension number = E164PhoneNumberWithExtension.ofPhoneNumberStringAndExtension("+441963350474", null, ISOCountryCode.DE);
		assertEquals(ISOCountryCode.GB, number.extractCountryCode());
	}

	@Test
	public void testGetCountryDiallingCode() {
		E164PhoneNumberWithExtension number = E164PhoneNumberWithExtension.ofPhoneNumberStringAndExtension("1963350474", null, ISOCountryCode.GB);
		assertEquals("+44", number.getCountryDiallingCode());
	}

	@Test
	public void testGetNationalNumber() {
		E164PhoneNumberWithExtension number = E164PhoneNumberWithExtension.ofPhoneNumberStringAndExtension("+441963350474", null, ISOCountryCode.DE);
		assertEquals("1963350474", number.getNationalNumber());
	}

	@Test
	public void testGetExtension() {
		E164PhoneNumberWithExtension number = E164PhoneNumberWithExtension.ofPhoneNumberStringAndExtension("+441963350474", null, ISOCountryCode.DE);
		assertNull(number.getExtension());
		
		number = E164PhoneNumberWithExtension.ofPhoneNumberStringAndExtension("+441963350474", "14354", ISOCountryCode.DE);
		assertEquals("14354", number.getExtension());
	}

	@Test
	public void testToE164NumberString() {
		E164PhoneNumberWithExtension number = E164PhoneNumberWithExtension.ofPhoneNumberStringAndExtension("1963350474", null, ISOCountryCode.GB);
		assertEquals("+441963350474", number.toE164NumberString());

	}

	@Test
	public void testDetermineAreaCode() {
		E164PhoneNumberWithExtension number = E164PhoneNumberWithExtension.ofPhoneNumberStringAndExtension("1963350474", null, ISOCountryCode.GB);
		assertEquals("1963", number.extractAreaCode());
	}

	@Test
	public void testDetermineSubscriberNumber() {
		E164PhoneNumberWithExtension number = E164PhoneNumberWithExtension.ofPhoneNumberStringAndExtension("1963350474", null, ISOCountryCode.GB);
		assertEquals("350474", number.extractSubscriberNumber());
	}

	@Test
	public void testToString() {
		E164PhoneNumberWithExtension number = E164PhoneNumberWithExtension.ofPhoneNumberStringAndExtension("+441963350474", null, ISOCountryCode.DE);
		assertEquals("+441963350474", number.toString());
		
		number = E164PhoneNumberWithExtension.ofPhoneNumberStringAndExtension("+441963350474", "14354", ISOCountryCode.DE);
		assertEquals("+441963350474;ext=14354", number.toString());
		
		number = E164PhoneNumberWithExtension.ofE164PhoneNumberWithExtensionString("+441963350474;ext=14354");
		assertEquals("+441963350474;ext=14354", number.toString());
	}
}
