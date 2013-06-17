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
package org.jadira.cloning.model;

import java.lang.reflect.Field;

import org.jadira.cloning.spi.FieldModel;
import org.jadira.cloning.unsafe.UnsafeOperations;

/**
 * Provides a model resulting from introspection of a field of a class, suitable for use with Unsafe
 */
public class UnsafeFieldModel extends PortableFieldModel implements FieldModel {

    private static final UnsafeOperations UNSAFE_OPERATIONS = UnsafeOperations.getUnsafeOperations();
    
    private final long offset;

    public UnsafeFieldModel(Field field) {

        super(field);
        this.offset = UNSAFE_OPERATIONS.getObjectFieldOffset(field);
    }
    
    public long getOffset() {
        return offset;
    }
}
