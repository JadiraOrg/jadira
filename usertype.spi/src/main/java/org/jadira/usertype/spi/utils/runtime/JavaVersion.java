package org.jadira.usertype.spi.utils.runtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a convenient capability to retrieve the version of Java
 */
public class JavaVersion {

    private static final Pattern JDK9_AND_HIGHER = Pattern.compile("([1-9][0-9]*)(\\.(\\d+))?.*");

    public static final int MAJOR_VERSION;
    public static final int MINOR_VERSION;

    static {
        Version version = parseVersion(System.getProperty("java.version"));
        MAJOR_VERSION = version.major;
        MINOR_VERSION = version.minor;
    }

    static Version parseVersion(String version) {
        if (version.startsWith("1.")) {
            String[] versions = version.split("\\.");
            if (versions.length <= 1) {
                throw new IllegalStateException("Invalid Java version: " + version);
            }
            return new Version(1, Integer.parseInt(versions[1]));
        } else {
            final Matcher matcher = JDK9_AND_HIGHER.matcher(version);
            if (matcher.matches()) {
                int major = Integer.parseInt(matcher.group(1));
                String minorGroup = matcher.group(3);
                int minor = minorGroup != null && !minorGroup.isEmpty() ? Integer.parseInt(minorGroup) : 0;
                return new Version(major, minor);
            } else {
                throw new IllegalStateException("Invalid Java version: " + version);
            }
        }
    }

    public static final int getMajorVersion() {
        return MAJOR_VERSION;
    }

    public static final int getMinorVersion() {
        return MINOR_VERSION;
    }

    public static boolean isJava8OrLater() {
        if (getMajorVersion() > 1) {
            return true;
        } else if (getMajorVersion() == 1 && getMinorVersion() >= 8) {
            return true;
        } else {
            return false;
        }
    }

    static class Version {
        final int major;
        final int minor;

        Version(int major, int minor) {
            this.major = major;
            this.minor = minor;
        }
    }
}
