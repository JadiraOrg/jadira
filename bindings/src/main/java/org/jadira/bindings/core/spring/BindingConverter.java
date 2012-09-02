/*
 *  Copyright 2010, 2011 Chris Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.bindings.core.spring;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.jadira.bindings.core.annotation.BindingScope;
import org.jadira.bindings.core.annotation.DefaultBinding;
import org.jadira.bindings.core.binder.BasicBinder;
import org.jadira.bindings.core.binder.ConverterKey;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

/**
 * Implementation of Spring's {@link ConditionalGenericConverter} that makes use
 * of Jadira Binding to perform conversions.
 */
public class BindingConverter implements ConditionalGenericConverter {

    private static final BasicBinder BINDING = new BasicBinder();
    
    /**
     * {@inheritDoc}
     */
    /* @Override */
    public Set<ConvertiblePair> getConvertibleTypes() {
        
        Set<ConvertiblePair> result = new HashSet<ConvertiblePair>();
        
		Iterable<ConverterKey<?,?>> entries = BINDING.getConverterEntries();
        for (ConverterKey<?,?> next : entries) {
            result.add(new ConvertiblePair(next.getInputClass(), next.getOutputClass()));
        }
        
        return result;
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public Object convert(Object object, TypeDescriptor sourceType, TypeDescriptor targetType) {
        
        final Object result;
        result = BINDING.convertTo(object.getClass(), targetType.getObjectType(), object, matchAnnotationToScope(targetType.getAnnotations()));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

        try {
            BINDING.findConverter(sourceType.getObjectType(), targetType.getObjectType(), matchAnnotationToScope(targetType.getAnnotations()));
            return true;
        } catch (IllegalStateException e) {
            return false;
        }    	
    }
    
    /**
     * Helper method for matching and returning a scope annotation 
     * @param annotations Annotations to inspect for a scope annotation
     * @return The matched annotation
     */
    private <T> Class<? extends Annotation> matchAnnotationToScope(Annotation[] annotations) {

        for (Annotation next : annotations) {
            Class<? extends Annotation> nextType = next.annotationType();
            if (nextType.getAnnotation(BindingScope.class) != null) {
                return nextType;
            }
        }
        return DefaultBinding.class;
    }
}
