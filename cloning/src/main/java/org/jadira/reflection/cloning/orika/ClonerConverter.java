/*
 * Copyright 2013 Chris Pheby
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jadira.reflection.cloning.orika;

import java.util.HashSet;
import java.util.Set;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

import org.jadira.reflection.cloning.BasicCloner;
import org.jadira.reflection.cloning.api.CloneStrategy;
import org.jadira.reflection.cloning.api.Cloner;

/**
 * This class is intended for use with Orika
 * <br>
 * ClonerConverter allows configuration of a number of specific types which should be copied
 * using BasicCloner.
 * <br>
 * This allows you to declare your own set of types which should be copied instead of mapped.
 * You can use this type with Orika independently of the rest of the cloning framework to perform
 * fast deep clones within Orika.
 */
public class ClonerConverter extends CustomConverter<Object, Object> {

    private Cloner cloner;
    
    private Set<Type<?>> types = new HashSet<Type<?>>();

    /**
     * Constructs a new ClonerConverter configured to handle the provided list of types by
     * cloning.
     * @param types one or more types that should be copied using the ClonerConverter
     */
    public ClonerConverter(java.lang.reflect.Type... types) {

        this(new BasicCloner(), types);
    }

    /**
     * Constructs a new ClonerConverter configured to handle the provided list of types by
     * cloning.
     * @param cloneStrategy Strategy to be used
     * @param types one or more types that should be copied using the ClonerConverter
     */
    public ClonerConverter(CloneStrategy cloneStrategy, java.lang.reflect.Type... types) {

        this.cloner = new BasicCloner(cloneStrategy);

        if (types.length == 0) {
            this.types = null;
        }
        for (java.lang.reflect.Type type : types) {
            this.types.add(TypeFactory.valueOf(type));
        }
    }

    
    /**
     * Constructs a new ClonerConverter configured to handle the provided list of types by
     * cloning.
     * @param cloner Cloner implementation to be used
     * @param types one or more types that should be copied using the ClonerConverter
     */
    public ClonerConverter(Cloner cloner, java.lang.reflect.Type... types) {

        this.cloner = cloner;

        if (types.length == 0) {
            this.types = null;
        }
        for (java.lang.reflect.Type type : types) {
            this.types.add(TypeFactory.valueOf(type));
        }
    }

    private boolean shouldCopy(Type<?> type) {
        
        if (types == null) {
            return true;
        }
        
        for (Type<?> registeredType : types) {
            if (registeredType.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
        return shouldCopy(sourceType) && sourceType.equals(destinationType);
    }

    @Override
    public Object convert(Object source, Type<? extends Object> destinationType, MappingContext context) {

        if (source == null) {
            return null;
        }
        return cloner.clone(source);
    }
}
