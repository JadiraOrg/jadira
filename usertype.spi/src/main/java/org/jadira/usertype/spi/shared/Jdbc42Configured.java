package org.jadira.usertype.spi.shared;


/**
 * Indicates a user type can be configured to use the JDBC 4.2 API
 */
public interface Jdbc42Configured {

	void setUseJdbc42Apis(boolean useJdbc42Apis);
}