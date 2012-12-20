package org.jadira.bindings.core.spring;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.jadira.bindings.core.test.SubjectC;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

public class BindingConverterTest {

    private GenericConversionService svc;
    
    @Before
    public void setup() {
        
        Set<GenericConverter> converters = new HashSet<GenericConverter>();
        converters.add(new BindingConverter());
        
        svc = new DefaultConversionService();
        ConversionServiceFactory.registerConverters(converters, svc);
    }
    
    /**
     * Test to prove that Jadira Convert can be invoked via Spring
     */
    @Test
    public void testBindingConverter() {
        assertEquals(new SubjectC("String"), svc.convert("String:String", SubjectC.class));
    }
}
