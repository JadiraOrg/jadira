package org.jadira.usertype.spi.utils.runtime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class JavaVersionTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"1.7.0_151-b15", new JavaVersion.Version(1, 7)},
                {"1.8.0_131", new JavaVersion.Version(1, 8)},
                {"9-ea+73", new JavaVersion.Version(9, 0)},
                {"9", new JavaVersion.Version(9, 0)},
                {"9+100", new JavaVersion.Version(9, 0)},
                {"9.0.1", new JavaVersion.Version(9, 0)},
                {"9.1.2", new JavaVersion.Version(9, 1)},
        });
    }

    private String version;
    private JavaVersion.Version expected;

    public JavaVersionTest(String version, JavaVersion.Version expected) {
        this.version = version;
        this.expected = expected;
    }

    @Test
    public void parseJavaVersion() {
        JavaVersion.Version parsedVersion = JavaVersion.parseVersion(version);
        assertEquals("Major version mismatch", expected.major, parsedVersion.major);
        assertEquals("Minor version mismatch", expected.minor, parsedVersion.minor);
    }
}
