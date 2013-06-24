package org.jadira.cloning;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.jadira.cloning.data.DeepCopyHolder;
import org.jadira.cloning.data.IdHolder;
import org.jadira.cloning.orika.ClonerConverter;
import org.junit.Assert;
import org.junit.Test;

public class ClonerConverterTest {

    @Test
    public void unsafeCopyConverter() throws DatatypeConfigurationException {

        ClonerConverter cc2 = new ClonerConverter(DeepCopyHolder.class);

        MapperFactory factory = new DefaultMapperFactory.Builder().build();
        factory.getConverterFactory().registerConverter(cc2);

        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, 10);
        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) cal);
        cal.add(Calendar.MONTH, 3);

        DeepCopyHolder source = new DeepCopyHolder();
        source.value = new IdHolder();
        source.value.setId("A Sample Value to Copy");
        source.timestamp = new Timestamp(System.currentTimeMillis() + 10000000);
        source.calendar = cal;
        source.xmlCalendar = xmlCal;

        DeepCopyHolder dest = factory.getMapperFacade().map(source, DeepCopyHolder.class);

        Assert.assertEquals(source.value, dest.value);
        Assert.assertNotSame(source.value, dest.value);
        Assert.assertEquals(source.timestamp, dest.timestamp);
        Assert.assertNotSame(source.timestamp, dest.timestamp);
        Assert.assertEquals(source.calendar, dest.calendar);
        Assert.assertNotSame(source.calendar, dest.calendar);
        Assert.assertEquals(source.xmlCalendar, dest.xmlCalendar);
        Assert.assertNotSame(source.xmlCalendar, dest.xmlCalendar);
    }    
}
