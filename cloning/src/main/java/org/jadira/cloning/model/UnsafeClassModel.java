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
import java.util.IdentityHashMap;
import java.util.Map;

import org.jadira.cloning.portable.ClassUtils;
import org.jadira.cloning.spi.ClassModel;

/**
 * Provides a model resulting from introspection of a class suitable for use with Unsafe
 */
public class UnsafeClassModel extends AbstractClassModel<UnsafeFieldModel> implements ClassModel<UnsafeFieldModel>{
    
    private static final Map<Class<?>, UnsafeClassModel> classModels = new IdentityHashMap<Class<?>, UnsafeClassModel>(100);
    
    private static final ThreadLocal<Map<Class<?>, UnsafeClassModel>> constructingClassModels = new ThreadLocal<Map<Class<?>, UnsafeClassModel>>() {
        public Map<Class<?>, UnsafeClassModel> initialValue() {
            return new IdentityHashMap<Class<?>, UnsafeClassModel>(100);
        }
    };

    private final UnsafeFieldModel[] modelFields;
    
    private UnsafeClassModel(Class<?> modelClass) {
        
        super(modelClass);

        Field[] fields = ClassUtils.collectFields(modelClass);        
        modelFields = new UnsafeFieldModel[fields.length];
        
        constructingClassModels.get().put(modelClass, this);
        
        for(int i=0; i<fields.length; i++) {
            UnsafeFieldModel modelEntry = new UnsafeFieldModel(fields[i]);
            modelFields[i] = modelEntry;
        }
        
        classModels.put(modelClass, this);
        constructingClassModels.get().remove(modelClass);
    }

    public static final UnsafeClassModel get(Class<?> clazz) {
        
        if(classModels.containsKey(clazz)) {
            return classModels.get(clazz);
        }
        if(constructingClassModels.get().containsKey(clazz)) {
            return constructingClassModels.get().get(clazz);
        }
        return new UnsafeClassModel(clazz);
    }

    public UnsafeFieldModel[] getModelFields() {
        return modelFields;
    }
}
