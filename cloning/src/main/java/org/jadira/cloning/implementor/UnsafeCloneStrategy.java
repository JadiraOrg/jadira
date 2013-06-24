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
package org.jadira.cloning.implementor;

import java.util.IdentityHashMap;

import org.jadira.cloning.api.CloneDriver;
import org.jadira.cloning.api.CloneStrategy;
import org.jadira.cloning.model.UnsafeClassModel;
import org.jadira.cloning.model.UnsafeFieldModel;
import org.jadira.cloning.portable.FieldType;
import org.jadira.cloning.unsafe.UnsafeOperations;
import org.objenesis.ObjenesisException;

public class UnsafeCloneStrategy extends AbstractCloneStrategy<UnsafeClassModel, UnsafeFieldModel> implements CloneStrategy {

    private static final UnsafeOperations UNSAFE_OPERATIONS = UnsafeOperations.getUnsafeOperations();
    
    @Override
    public <T> T newInstance(Class<T> c) {
        try {
            return UNSAFE_OPERATIONS.allocateInstance(c);
        } catch (IllegalStateException e) {
            throw new ObjenesisException(e.getCause());
        }
    }

    private static UnsafeCloneStrategy instance = new UnsafeCloneStrategy();

    public static UnsafeCloneStrategy getInstance() {
        return instance;
    }
    
    @Override
    protected UnsafeClassModel getClassModel(Class<?> clazz) {
        return UnsafeClassModel.get(clazz);
    }
    
    @Override
    protected <T> void handleTransientField(T copy, UnsafeFieldModel f) {
        if (f.getFieldType() == FieldType.PRIMITIVE) {
            UNSAFE_OPERATIONS.putPrimitiveDefaultAtOffset(copy, f.getFieldClass(), f.getOffset());
        } else {
            UNSAFE_OPERATIONS.putNullObject(copy, f.getOffset());
        }
    }

    @Override
    protected <T> void handleClonePrimitiveField(T obj, T copy, CloneDriver driver, UnsafeFieldModel f,
            IdentityHashMap<Object, Object> referencesToReuse) {
        UNSAFE_OPERATIONS.copyPrimitiveAtOffset(obj, copy, f.getFieldClass(), f.getOffset());
    }

    @Override
    protected <T> Object getFieldValue(T obj, UnsafeFieldModel f, IdentityHashMap<Object, Object> referencesToReuse) {
        return UNSAFE_OPERATIONS.getObject(obj, f.getOffset());
    }

    @Override
    protected <T> void putFieldValue(T obj, UnsafeFieldModel f, Object value) {
        if (value == null) {
            UNSAFE_OPERATIONS.putNullObject(obj, f.getOffset());
        } else {
            UNSAFE_OPERATIONS.putObject(obj, f.getOffset(), value);
        }
    }
}
