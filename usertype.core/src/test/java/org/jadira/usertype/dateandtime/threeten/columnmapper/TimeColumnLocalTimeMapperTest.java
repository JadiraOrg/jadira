package org.jadira.usertype.dateandtime.threeten.columnmapper;

import org.junit.Before;
import org.junit.Test;

import java.sql.Time;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class TimeColumnLocalTimeMapperTest
{
  private TimeColumnLocalTimeMapper timeColumnLocalTimeMapper;

  @Before
  public void setup()
  {
    timeColumnLocalTimeMapper = new TimeColumnLocalTimeMapper();
  }
  @Test
  public void toNonNullValueShouldReturnTheCorrectTime()
  {
    LocalTime localTime = LocalTime.of(13, 37);
    Time expected = Time.valueOf("13:37:00");
    Time time = timeColumnLocalTimeMapper.toNonNullValue(localTime);
    assertEquals(expected, time);
  }
}