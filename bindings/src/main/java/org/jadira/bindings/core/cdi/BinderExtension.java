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
package org.jadira.bindings.core.cdi;

import java.lang.annotation.Annotation;
import java.net.URL;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.api.Converter;
import org.jadira.bindings.core.api.FromUnmarshaller;
import org.jadira.bindings.core.api.ToMarshaller;
import org.jadira.bindings.core.binder.BasicBinder;
import org.jadira.bindings.core.binder.Binder;
import org.jadira.bindings.core.binder.ConverterKey;
import org.jadira.bindings.core.binder.RegisterableBinder;

/**
 * A CDI Portable Extension that inspects beans being loaded by the container for bindings.
 */
public class BinderExtension implements Extension, Binder, RegisterableBinder {

	/**
	 * Wrapped default binder
	 */
    private static final BasicBinder BINDING = new BasicBinder(); 
    
    /**
     * Create a new instance of BinderExtension
     */
    public BinderExtension() {}
    
    /**
     * {@inheritDoc}
     */
    /* @Override */
    public <T, E> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {

        Class<?> candidateClass = pat.getAnnotatedType().getJavaClass();

        if (!candidateClass.isInterface()) {

            try {
                if (Binding.class.isAssignableFrom(candidateClass)) {

                    @SuppressWarnings("unchecked")
                    Class<? extends Binding<T, E>> bindingClass = (Class<? extends Binding<T, E>>) candidateClass;
                    Binding<T, E> myBinding = bindingClass.newInstance();
                    BINDING.registerBinding(myBinding.getBoundClass(), myBinding.getTargetClass(), myBinding);
                } else if (ToMarshaller.class.isAssignableFrom(candidateClass)
                        || FromUnmarshaller.class.isAssignableFrom(candidateClass)) {
                    if (ToMarshaller.class.isAssignableFrom(candidateClass)) {

                        @SuppressWarnings("unchecked")
                        Class<? extends ToMarshaller<T,E>> bindingClass = (Class<? extends ToMarshaller<T,E>>) candidateClass;
                        ToMarshaller<T,E> myBinding = bindingClass.newInstance();
                        BINDING.registerMarshaller(myBinding.getBoundClass(), myBinding.getTargetClass(), myBinding);
                    }
                    if (FromUnmarshaller.class.isAssignableFrom(candidateClass)) {

                        @SuppressWarnings("unchecked")
                        Class<? extends FromUnmarshaller<T,E>> bindingClass = (Class<? extends FromUnmarshaller<T,E>>) candidateClass;
                        FromUnmarshaller<T,E> myBinding = bindingClass.newInstance();
                        BINDING.registerUnmarshaller(myBinding.getBoundClass(), myBinding.getTargetClass(), myBinding);
                    }
                } else if (Converter.class.isAssignableFrom(candidateClass)) {

                    @SuppressWarnings("unchecked")
                    Class<? extends Converter<T, E>> bindingClass = (Class<? extends Converter<T, E>>) candidateClass;
                    Converter<T,E> myConverter = bindingClass.newInstance();
                    BINDING.registerConverter(myConverter.getInputClass(), myConverter.getOutputClass(), myConverter);
                } else {
                	BINDING.registerAnnotatedClasses(candidateClass);
                }
            } catch (InstantiationException e) {
                throw new IllegalStateException("Cannot instantiate binding class: " + candidateClass);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot instantiate binding class: " + candidateClass);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> T convertTo(Class<T> output, Object object) {
		return BINDING.convertTo(output, object);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> T convertTo(Class<T> output, Object object,
			Class<? extends Annotation> qualifier) {
		return BINDING.convertTo(output, object, qualifier);
	}
    
    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> T convertTo(Class<S> sourceClass, Class<T> target, Object object) {
		return BINDING.convertTo(sourceClass, target, object);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> T convertTo(Class<S> sourceClass, Class<T> target, Object object, Class<? extends Annotation> qualifier) {
		return BINDING.convertTo(sourceClass, target, object, qualifier);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> T convertTo(ConverterKey<S,T> key, Object object) {
		return BINDING.convertTo(key, object);
	}
	
    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> Binding<S, T> findBinding(Class<S> source, Class<T> target) {
		return BINDING.findBinding(source, target);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> ToMarshaller<S, T> findMarshaller(Class<S> source, Class<T> target) {
		return BINDING.findMarshaller(source, target);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> Converter<S, T> findConverter(Class<S> source, Class<T> target) {
		return BINDING.findConverter(source, target);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> FromUnmarshaller<S, T> findUnmarshaller(Class<S> source, Class<T> target) {
		return BINDING.findUnmarshaller(source, target);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> Binding<S, T> findBinding(Class<S> source, Class<T> target, Class<? extends Annotation> qualifier) {
		return BINDING.findBinding(source, target, qualifier);
	}
	
    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> Binding<S,T> findBinding(ConverterKey<S,T> key) {
		return BINDING.findBinding(key);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> ToMarshaller<S, T> findMarshaller(Class<S> source, Class<T> target, Class<? extends Annotation> qualifier) {
		return BINDING.findMarshaller(source, target, qualifier);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> ToMarshaller<S,T> findMarshaller(ConverterKey<S,T> key) {
		return BINDING.findMarshaller(key);
	}
	
    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> Converter<S, T> findConverter(Class<S> source, Class<T> target, Class<? extends Annotation> qualifier) {
		return BINDING.findConverter(source, target, qualifier);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> Converter<S,T> findConverter(ConverterKey<S,T> key) {
		return BINDING.findConverter(key);
	}
	
    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> FromUnmarshaller<S, T> findUnmarshaller(Class<S> source, Class<T> target, Class<? extends Annotation> qualifier) {
		return BINDING.findUnmarshaller(source, target, qualifier);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> FromUnmarshaller<S,T> findUnmarshaller(ConverterKey<S,T> key) {
		return BINDING.findUnmarshaller(key);
	}
	
    /**
     * {@inheritDoc}
     */
    /* @Override */
    public void registerConfiguration(URL nextLocation) {
        BINDING.registerConfiguration(nextLocation);
    }
	
    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerBinding(Class<S> sourceClass, Class<T> targetClass, Binding<S, T> converter) {
		BINDING.registerBinding(sourceClass, targetClass, converter);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerUnmarshaller(Class<S> sourceClass, Class<T> targetClass, FromUnmarshaller<S, T> converter) {
		BINDING.registerUnmarshaller(sourceClass, targetClass, converter);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerMarshaller(Class<S> sourceClass, Class<T> targetClass, ToMarshaller<S, T> converter) {
		BINDING.registerMarshaller(sourceClass, targetClass, converter);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerConverter(Class<S> sourceClass, Class<T> targetClass, Converter<S, T> converter) {
		BINDING.registerConverter(sourceClass, targetClass, converter);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerBinding(Class<S> sourceClass, Class<T> targetClass, Binding<S, T> converter, Class<? extends Annotation> qualifier) {
		BINDING.registerBinding(sourceClass, targetClass, converter, qualifier);
	}
	
    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerBinding(ConverterKey<S,T> key, Binding<S, T> converter) {
		BINDING.registerBinding(key, converter);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerUnmarshaller(Class<S> sourceClass, Class<T> targetClass, FromUnmarshaller<S, T> converter, Class<? extends Annotation> qualifier) {
		BINDING.registerUnmarshaller(sourceClass, targetClass, converter, qualifier);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerUnmarshaller(ConverterKey<S,T> key, FromUnmarshaller<S, T> converter) {
		BINDING.registerUnmarshaller(key, converter);
	}
	
    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerMarshaller(Class<S> sourceClass, Class<T> targetClass, ToMarshaller<S, T> converter, Class<? extends Annotation> qualifier) {
		BINDING.registerMarshaller(sourceClass, targetClass, converter, qualifier);	
	}
	
    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerMarshaller(ConverterKey<S,T> key, ToMarshaller<S, T> converter) {
		BINDING.registerMarshaller(key, converter);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerConverter(Class<S> sourceClass, Class<T> targetClass, Converter<S, T> converter, Class<? extends Annotation> qualifier) {
		BINDING.registerConverter(sourceClass, targetClass, converter, qualifier);
	}
	
    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S, T> void registerConverter(ConverterKey<S,T> key, Converter<S, T> converter) {
		BINDING.registerConverter(key, converter);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public void registerAnnotatedClasses(Class<?>... classesToInspect) {
		BINDING.registerAnnotatedClasses(classesToInspect);
	}
	
    /**
     * {@inheritDoc}
     */
    /* @Override */
	public Iterable<ConverterKey<?,?>> getConverterEntries() {
		return BINDING.getConverterEntries();
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <T> T convertFromString(Class<T> output, String object) {
		return BINDING.convertFromString(output, object);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <T> T convertFromString(Class<T> output, String object,
			Class<? extends Annotation> qualifier) {
		return BINDING.convertFromString(output, object, qualifier);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public String convertToString(Object object) {
		return BINDING.convertToString(object);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public String convertToString(Object object,
			Class<? extends Annotation> qualifier) {
		return BINDING.convertToString(object, qualifier);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S> String convertToString(Class<S> input, Object object) {
		return BINDING.convertToString(input, object);
	}

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public <S> String convertToString(Class<S> input, Object object,
			Class<? extends Annotation> qualifier) {
		return BINDING.convertToString(input, object, qualifier);
	}
}