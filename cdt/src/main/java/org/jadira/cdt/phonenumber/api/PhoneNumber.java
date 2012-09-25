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
    public String getCountryDiallingCode();
    
	/**
	 * Returns the national number part for the phone number.
	 * @return The country dialling code e.g. '1963350474'
	 */    
    public String getNationalNumber();

	/**
	 * Returns the extension for the phone number or null.
	 * @return The extension
	 */
    public String getExtension();

	/**
	 * Returns the E164 formatted telephone number. Any extension is ommitted.
	 * @return The telephone number formatted as E164.
	 */
    public String toE164NumberString();

	/**
	 * Returns the E164 formatted telephone number. Any extension is appended to the number
	 * with the extension prefix given as ';ext='
	 * @return The telephone number formatted as E164 together with the extension, if any.
	 */
    public String toE164NumberWithExtensionString();

    /**
     * Returns the CountryCode this number belongs to
     * @return The CountryCode
     */
    public CountryCode extractCountryCode();
    
    /**
     * Splits the national number into its constituent parts, returning that part of the number that
     * represents the area code.
     * @return The area code
     */
    public String extractAreaCode();

    /**
     * Splits the national number into its constituent parts, returning that part of the number that
     * represents the subscriber number.
     * @return The subscriber number
     */
    public String extractSubscriberNumber();
}
