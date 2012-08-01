package org.jadira.cdt.country;

public interface ISOCountryCode {

	/**
	 * Get the country name.
	 * @return The country name.
	 */
	String getName();

	/**
	 * Get the <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2" >ISO 3166-1 alpha-2</a> code.
	 * @return The <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">ISO 3166-1 alpha-2</a> code.
	 */
	String getAlpha2();

	/**
	 * Get the <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3" >ISO 3166-1 alpha-3</a> code.
	 * @return The <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3">ISO 3166-1 alpha-3</a> code.
	 */
	String getAlpha3();

	/**
	 * Get the <a href="http://en.wikipedia.org/wiki/ISO_3166-1_numeric" >ISO 3166-1 numeric</a> code.
	 * @return The <a href="http://en.wikipedia.org/wiki/ISO_3166-1_numeric">ISO 3166-1 numeric</a> code.
	 */
	int getNumeric();

	/**
	 * Gets the <a href="http://en.wikipedia.org/wiki/E.164">International Direct Dial</a> prefix for the country
	 * @return
	 */
	int getDiallingRegionCode();

}