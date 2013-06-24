/*
 *  Copyright 2013 Chris Pheby
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
package org.jadira.cloning.spi;

import java.lang.reflect.Field;

import org.jadira.cloning.portable.FieldType;

/**
 * Provides a model resulting from introspection of a field of a class
 */
public interface FieldModel {

    Field getField();

    FieldType getFieldType();

    Class<?> getFieldClass();

    boolean isTransientField();

    boolean isTransientAnnotatedField();
    
    boolean isSynthetic();
}