package org.jadira.usertype.country;

import org.jadira.cdt.country.ISOCountryCode;
import org.jadira.usertype.country.columnmapper.StringColumnISOCountryCodeMapper;
import org.jadira.usertype.spi.shared.AbstractSingleColumnUserType;

public class PersistentISOCountryCode extends AbstractSingleColumnUserType<ISOCountryCode, String, StringColumnISOCountryCodeMapper> {

	private static final long serialVersionUID = -306531073551665558L;
}
