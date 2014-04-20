package org.jadira.usertype.spi.utils.runtime;

/**
 * Provides a convenient capability to retrieve the version of Java
 */
public class JavaVersion {

    public static final int MAJOR_VERSION;
    public static final int MINOR_VERSION;

    static {
        String version = System.getProperty("java.version");
        String[] versions = version.split("[.]");

        MAJOR_VERSION = Integer.parseInt(versions[0]);
        MINOR_VERSION = Integer.parseInt(versions[1]);
    }

    public static final int getMajorVersion() {
        return MAJOR_VERSION;
    }
    
    public static final int getMinorVersion() {
        return MINOR_VERSION;
    }
}
