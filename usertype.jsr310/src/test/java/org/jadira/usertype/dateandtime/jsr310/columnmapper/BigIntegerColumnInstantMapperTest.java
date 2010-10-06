package org.jadira.usertype.dateandtime.jsr310.columnmapper;

import static org.junit.Assert.assertEquals;

import javax.time.Instant;

import org.junit.Test;

public class BigIntegerColumnInstantMapperTest {

    @Test
    public void testReturnedClass() {
        assertEquals(Instant.class, new BigIntegerColumnInstantMapper().returnedClass());
    }
}
