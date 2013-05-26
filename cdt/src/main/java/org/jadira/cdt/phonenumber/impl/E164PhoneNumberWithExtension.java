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

import java.io.Serializable;

import org.jadira.bindings.core.annotation.typesafe.FromString;
import org.jadira.bindings.core.annotation.typesafe.ToString;
import org.jadira.cdt.country.CountryCode;
import org.jadira.cdt.country.ISOCountryCode;
import org.jadira.cdt.phonenumber.api.PhoneNumber;
import org.jadira.cdt.phonenumber.api.PhoneNumberParseException;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * This class represents a phone number with a broadly canonical rendering.
 * The default form is to use E164 format with the addition of an optional extension.
 * Extension is signified with a suffix of &quot;;ext=&quot;.
 * 
 * The class uses Google's excellent libphonenumber for its underlying representation.
 * This gives it great flexibility in accomodating existing legacy number formats and
 * converting them to a standardised notation.
 * 
 * The class does not expose directly the wrapped PhoneNumber instance as it is 
 * immutable. However, it is possible to retrieve a copy for further manipulation.
 */
public class E164PhoneNumberWithExtension implements PhoneNumber, Serializable {

	private static final long serialVersionUID = -7665314825162464754L;

	private static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();
    
    private static final String RFC3966_EXTN_PREFIX = ";ext=";

    private Phonenumber.PhoneNumber number;

	private static final String EX_PARSE_MSG_PREFIX = "Could not parse {";
    
    /**
     * Creates a instance from the given PhoneNumber
     * @param prototype The PhoneNumber to construct the instance from
     */
    public E164PhoneNumberWithExtension(Phonenumber.PhoneNumber prototype) {

        StringBuilder e164Builder = new StringBuilder();
        PHONE_NUMBER_UTIL.format(prototype, PhoneNumberFormat.E164, e164Builder);
        
    	try {
            this.number = PHONE_NUMBER_UTIL.parse(e164Builder.toString(), "GB");
        } catch (NumberParseException e) {
            throw new PhoneNumberParseException(
                    EX_PARSE_MSG_PREFIX + prototype.getNationalNumber() +"}", e);
        }
    	
    	if (prototype.hasExtension()) {
    		this.number.setExtension(prototype.getExtension());
    	}
    }

    /**
     * Creates a new E164 Phone Number with the given extension.
     * @param e164PhoneNumber The phone number in E164 format. The extension may be appended.  
     * Any extension is appended to the number with the extension prefix given as ';ext='
     * @return A new instance of E164PhoneNumberWithExtension
     */
    protected E164PhoneNumberWithExtension(String e164PhoneNumberWithExtension) {

        if (!e164PhoneNumberWithExtension.startsWith("+")) {
            throw new PhoneNumberParseException("Only international numbers can be interpreted without a country code");
        }
        
        final String e164PhoneNumber;
        final String extension;
        if (e164PhoneNumberWithExtension.contains(RFC3966_EXTN_PREFIX)) {
        	extension = e164PhoneNumberWithExtension.substring(e164PhoneNumberWithExtension.indexOf(RFC3966_EXTN_PREFIX) + RFC3966_EXTN_PREFIX.length());
        	e164PhoneNumber = e164PhoneNumberWithExtension.substring(0, e164PhoneNumberWithExtension.indexOf(RFC3966_EXTN_PREFIX));
        } else {
        	extension = null;
        	e164PhoneNumber = e164PhoneNumberWithExtension;
        }

        try {
            number = PHONE_NUMBER_UTIL.parse(e164PhoneNumber, "GB");
        } catch (NumberParseException e) {
            throw new PhoneNumberParseException(
                    EX_PARSE_MSG_PREFIX + e164PhoneNumber +"}", e);
        }
        
        if (extension != null) {
        	number.setExtension(extension);
        }
    }

    /**
     * Creates a new E164 Phone Number with the given extension.
     * @param e164PhoneNumber The phone number in E164 format
     * @param extension The extension, or null for no extension
     * @return A new instance of E164PhoneNumberWithExtension
     */
    protected E164PhoneNumberWithExtension(String e164PhoneNumber, String extension) {

        if (!e164PhoneNumber.startsWith("+")) {
            throw new PhoneNumberParseException("Only international numbers can be interpreted without a country code");
        }
        
        try {
            number = PHONE_NUMBER_UTIL.parse(e164PhoneNumber, "GB");
        } catch (NumberParseException e) {
            throw new PhoneNumberParseException(
                    EX_PARSE_MSG_PREFIX + e164PhoneNumber +"}", e);
        }
        
        number.setExtension(extension);
    }
    
    /**
     * Creates a new E164 Phone Number.
     * @param phoneNumber The phone number in arbitrary parseable format (may be a national format)
     * @param defaultCountryCode The Country to apply if no country is indicated by the phone number
     * @return A new instance of E164PhoneNumberWithExtension
     */
    protected E164PhoneNumberWithExtension(String phoneNumber, CountryCode defaultCountryCode) {

        try {
            number = PHONE_NUMBER_UTIL.parse(phoneNumber, defaultCountryCode.toString());
        } catch (NumberParseException e) {
            throw new PhoneNumberParseException(
                    EX_PARSE_MSG_PREFIX + phoneNumber + "} for country {" + defaultCountryCode +"}", e);
        }
    }

    /**
     * Creates a new E164 Phone Number with the given extension.
     * @param phoneNumber The phone number in arbitrary parseable format (may be a national format)
     * @param extension The extension, or null for no extension
     * @param defaultCountryCode The Country to apply if no country is indicated by the phone number
     * @return A new instance of E164PhoneNumberWithExtension
     */
    protected E164PhoneNumberWithExtension(String phoneNumber, String extension, CountryCode defaultCountryCode) {

        try {
            number = PHONE_NUMBER_UTIL.parse(phoneNumber, defaultCountryCode.toString());
        } catch (NumberParseException e) {
            throw new PhoneNumberParseException(
                    EX_PARSE_MSG_PREFIX + phoneNumber + "} for country {" + defaultCountryCode +"}", e);
        }
        if (extension != null) {
        	number.setExtension(extension);
        }
    }

    /**
     * Creates a new E164 Phone Number.
     * @param e164PhoneNumber The phone number in E164 format.
     * @return A new instance of E164PhoneNumberWithExtension
     */
    public static E164PhoneNumberWithExtension ofE164PhoneNumberString(String e164PhoneNumber) {
    	return new E164PhoneNumberWithExtension(e164PhoneNumber, (String)null);
    }

    /**
     * Creates a new E164 Phone Number with the given extension.
     * @param e164PhoneNumber The phone number in E164 format. The extension may be appended.  
     * Any extension is appended to the number with the extension prefix given as ';ext='
     * @return A new instance of E164PhoneNumberWithExtension
     */
    @FromString
    public static E164PhoneNumberWithExtension ofE164PhoneNumberWithExtensionString(String e164PhoneNumber) {
    	return new E164PhoneNumberWithExtension(e164PhoneNumber);
    }

    /**
     * Creates a new E164 Phone Number with the given extension.
     * @param e164PhoneNumber The phone number in E164 format
     * @param extension The extension, or null for no extension
     * @return A new instance of E164PhoneNumberWithExtension
     */
    public static E164PhoneNumberWithExtension ofE164PhoneNumberStringAndExtension(String e164PhoneNumber, String extension) {
    	return new E164PhoneNumberWithExtension(e164PhoneNumber, extension);
    }
    
    /**
     * Creates a new E164 Phone Number.
     * @param phoneNumber The phone number in arbitrary parseable format (may be a national format)
     * @param defaultCountryCode The Country to apply if no country is indicated by the phone number
     * @return A new instance of E164PhoneNumberWithExtension
     */
    public static E164PhoneNumberWithExtension ofPhoneNumberString(String phoneNumber, CountryCode defaultCountryCode) {
    	return new E164PhoneNumberWithExtension(phoneNumber, defaultCountryCode);
    }
    
    /**
     * Creates a new E164 Phone Number with the given extension.
     * @param phoneNumber The phone number in arbitrary parseable format (may be a national format)
     * @param extension The extension, or null for no extension
     * @param defaultCountryCode The Country to apply if no country is indicated by the phone number
     * @return A new instance of E164PhoneNumberWithExtension
     */
    public static E164PhoneNumberWithExtension ofPhoneNumberStringAndExtension(String phoneNumber, String extension, CountryCode defaultCountryCode) {
    	return new E164PhoneNumberWithExtension(phoneNumber, extension, defaultCountryCode);
    }
    
    /**
     * Returns the underlying LibPhoneNumber {@link Phonenumber.PhoneNumber} instance. 
     * To preserve the immutability of E164PhoneNumber, a copy is made.
     */
    public Phonenumber.PhoneNumber getUnderlyingPhoneNumber() {
        
    	Phonenumber.PhoneNumber copy;
    	
        StringBuilder e164Builder = new StringBuilder();
        PHONE_NUMBER_UTIL.format(number, PhoneNumberFormat.E164, e164Builder);
        
    	try {
            copy = PHONE_NUMBER_UTIL.parse(e164Builder.toString(), "GB");
        } catch (NumberParseException e) {
            throw new PhoneNumberParseException(
                    EX_PARSE_MSG_PREFIX + number.getNationalNumber() +"}", e);
        }
    	
    	if (number.hasExtension()) {
    		copy.setExtension(number.getExtension());
    	}
    	return copy;
    }

	/**
	 * {@inheritDoc}
	 */
    public ISOCountryCode extractCountryCode() {
        return ISOCountryCode.valueOf(PHONE_NUMBER_UTIL.getRegionCodeForNumber(number));
    }

	/**
	 * {@inheritDoc}
	 */
    public String getCountryDiallingCode() {
        return "+" + number.getCountryCode();
    }

	/**
	 * {@inheritDoc}
	 */
    public String getNationalNumber() {
        return "" + number.getNationalNumber();
    }

	/**
	 * {@inheritDoc}
	 */
    public String getExtension() {
        return number.hasExtension() ? number.getExtension() : null;
    }

	/**
	 * {@inheritDoc}
	 */
    public String toE164NumberString() {
        StringBuilder result = new StringBuilder();
        PHONE_NUMBER_UTIL.format(number, PhoneNumberFormat.E164, result);
        
        return result.toString();
    }
    
	/**
	 * {@inheritDoc}
	 */
    @ToString
    public String toE164NumberWithExtensionString() {
    	
    	StringBuilder formattedString = new StringBuilder(toE164NumberString());
    	
    	if(number.hasExtension()) {
    		formattedString.append(RFC3966_EXTN_PREFIX);
    		formattedString.append(number.getExtension());
    	}
    	
    	return formattedString.toString();
    }
    
	/**
	 * {@inheritDoc}
	 */
    public String extractAreaCode() {
        
		final String nationalSignificantNumber = PHONE_NUMBER_UTIL.getNationalSignificantNumber(number);
		final String areaCode;

		int areaCodeLength = PHONE_NUMBER_UTIL.getLengthOfGeographicalAreaCode(number);
		if (areaCodeLength > 0) {
			areaCode = nationalSignificantNumber.substring(0, areaCodeLength);
		} else {
			areaCode = "";
		}

		return areaCode;
    }

	/**
	 * {@inheritDoc}
	 */
    public String extractSubscriberNumber() {
        
		final String nationalSignificantNumber = PHONE_NUMBER_UTIL.getNationalSignificantNumber(number);
		final String subscriberNumber;

		int areaCodeLength = PHONE_NUMBER_UTIL.getLengthOfGeographicalAreaCode(number);
		if (areaCodeLength > 0) {
			subscriberNumber = nationalSignificantNumber.substring(areaCodeLength);
		} else {
			subscriberNumber = nationalSignificantNumber;
		}

		return subscriberNumber;
    }    

	/**
	 * {@inheritDoc}
	 */
    @Override
    public String toString() {
    	return toE164NumberWithExtensionString();
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}

		final E164PhoneNumberWithExtension obj2 = (E164PhoneNumberWithExtension) obj;
		if (this.toString().equals(obj2.toString())) {
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
