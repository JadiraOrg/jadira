package org.jadira.usertype.dateandtime.joda.columnmapper;
import org.jadira.usertype.dateandtime.joda.columnmapper.TimestampColumnDateTimeMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TimestampColumnDateTimeMapperTest {

    final DateTimeZone AMERICA_SAOPAULO_ZONE = DateTimeZone.forID("America/Sao_Paulo");
    final DateTimeZone UTC_ZONE = DateTimeZone.UTC;

    @Before
    public void setup() {
        // Mapper (SUT) should ignore this default time zone when .setJavaZone(..) is used!
        // All tests always set a specific JavaZone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));//"Asia/Shanghai"));
    }


    @Test
    public void toNonNullValue_UTC() throws Exception {

        //
        // fixture
        TimestampColumnDateTimeMapper mapper = new TimestampColumnDateTimeMapper();
        mapper.setJavaZone(UTC_ZONE);
        mapper.setDatabaseZone(UTC_ZONE);

        //
        // exercise SUT
        Timestamp timestamp = mapper.toNonNullValue(new DateTime(2013, 10, 22, 0, 0, 0, 0, UTC_ZONE));

        //
        // verify
        System.out.println(timestamp);
        assertThat(timestamp.getTime(), is(new DateTime(2013, 10, 22, 0, 0, 0, 0, UTC_ZONE).getMillis()));
        // assertThat(timestamp.toString(), is(new DateTime(2013, 10, 22, 0, 0, 0, 0, UTC_ZONE).toString() + "00Z"));

    }

    @Test
    public void toNonNullValue_AmericaSaoPaulo() throws Exception {

        //
        // fixture
        TimestampColumnDateTimeMapper mapper = new TimestampColumnDateTimeMapper();
        mapper.setJavaZone(AMERICA_SAOPAULO_ZONE);
        mapper.setDatabaseZone(DateTimeZone.UTC);

        //
        // exercise SUT
        Timestamp timestamp = mapper.toNonNullValue(new DateTime(2013, 10, 1, 0, 0, 0, 0, AMERICA_SAOPAULO_ZONE));

        //
        // verify
        assertThat(timestamp.getTime(), is(new DateTime(2013, 10, 1, 0, 0, 0, 0, AMERICA_SAOPAULO_ZONE).getMillis()));

    }

    @Test
    public void toNonNullValue_AmericaSaoPaulo_DaylightSaving() throws Exception {

        //
        // fixture
        TimestampColumnDateTimeMapper mapper = new TimestampColumnDateTimeMapper();
        mapper.setJavaZone(AMERICA_SAOPAULO_ZONE);
        mapper.setDatabaseZone(DateTimeZone.UTC);

        //
        // exercise SUT
        Timestamp timestamp = mapper.toNonNullValue(new DateTime(2013, 10, 22, 0, 0, 0, 0, AMERICA_SAOPAULO_ZONE));

        //
        // verify
        assertThat(timestamp.getTime(), is(new DateTime(2013, 10, 22, 0, 0, 0, 0, AMERICA_SAOPAULO_ZONE).getMillis()));

    }


    @Test
    public void fromNonNullValue_UTC() throws Exception {

        //
        // fixture
        TimestampColumnDateTimeMapper mapper = new TimestampColumnDateTimeMapper();
        mapper.setJavaZone(UTC_ZONE);
        mapper.setDatabaseZone(UTC_ZONE);

        Timestamp timestamp = new Timestamp(new DateTime(2013, 10, 22, 2, 0, 0, 0, UTC_ZONE).getMillis());

        //
        // exercise SUT
        DateTime dateTime = mapper.fromNonNullValue(timestamp);

        //
        // verify
        assertThat(dateTime, is(new DateTime(2013, 10, 22, 2, 0, 0, 0, UTC_ZONE)));
    }

    @Test
    public void fromNonNullValue_AmericaSaoPaulo() throws Exception {

        //
        // fixture
        TimestampColumnDateTimeMapper mapper = new TimestampColumnDateTimeMapper();
        mapper.setJavaZone(AMERICA_SAOPAULO_ZONE);
        mapper.setDatabaseZone(DateTimeZone.UTC);

        Timestamp timestamp = new Timestamp(new DateTime(2013, 10, 1, 2, 0, 0, 0, AMERICA_SAOPAULO_ZONE).getMillis());

        //
        // exercise SUT
        DateTime dateTime = mapper.fromNonNullValue(timestamp);

        //
        // verify
        assertThat(dateTime, is(new DateTime(2013, 10, 1, 2, 0, 0, 0, AMERICA_SAOPAULO_ZONE)));
    }

    @Test
    public void fromNonNullValue_AmericaSaoPaulo_DaylightSaving() throws Exception {

        //
        // fixture
        TimestampColumnDateTimeMapper mapper = new TimestampColumnDateTimeMapper();
        mapper.setJavaZone(AMERICA_SAOPAULO_ZONE);
        mapper.setDatabaseZone(DateTimeZone.UTC);

        Timestamp timestamp = new Timestamp(new DateTime(2013, 10, 22, 2, 0, 0, 0, AMERICA_SAOPAULO_ZONE).getMillis());

        //
        // exercise SUT
        DateTime dateTime = mapper.fromNonNullValue(timestamp);

        //
        // verify
        assertThat(dateTime, is(new DateTime(2013, 10, 22, 2, 0, 0, 0, AMERICA_SAOPAULO_ZONE)));
    }

    @Test
    public void fromNonNullValue_AmericaSaoPaulo_DaylightSavingTransitionInstant() throws Exception {

        //
        // fixture
        TimestampColumnDateTimeMapper mapper = new TimestampColumnDateTimeMapper();
        mapper.setJavaZone(AMERICA_SAOPAULO_ZONE);
        mapper.setDatabaseZone(DateTimeZone.UTC);

        long millisAtTransitionInstant = new DateTime(2013, 10, 19, 23, 59, 59, AMERICA_SAOPAULO_ZONE).getMillis() + 1000;
        Timestamp timestamp = new Timestamp(millisAtTransitionInstant);

        //
        // exercise SUT
        DateTime dateTime = mapper.fromNonNullValue(timestamp);

        //
        // verify
        // At 'time zone offset transition' (2013-10-19 23:59:59 + 1 second), DateTime should be 2013-10-20 01:00:00.
        assertThat(dateTime, is(new DateTime(2013, 10, 20, 1, 0, 0, 0, AMERICA_SAOPAULO_ZONE)));
    }

}