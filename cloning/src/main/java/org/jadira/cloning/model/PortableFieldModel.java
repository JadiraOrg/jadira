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
import java.lang.reflect.Modifier;

import org.jadira.cloning.annotation.Transient;
import org.jadira.cloning.portable.FieldType;
import org.jadira.cloning.spi.FieldModel;

/**
 * Provides a model resulting from introspection of a field of a class suitable for use with Java Reflection
 */
public class PortableFieldModel implements FieldModel {
    
    private final Field field;
    private final FieldType fieldType;
    private final Class<?> fieldClass;

    private final boolean transientField;
    private final boolean transientAnnotatedField;
	
    private final boolean isSynthetic;

    public PortableFieldModel(Field field) {
        
        this.field = field;
        if (field.getType().isPrimitive()) {
            this.fieldType = FieldType.PRIMITIVE;
        } else if (field.getType().isArray()) {
            this.fieldType = FieldType.ARRAY;
        } else {
            this.fieldType = FieldType.OBJECT;
        }
        this.fieldClass = field.getType();
        
        this.transientField = Modifier.isTransient(field.getModifiers());
        this.transientAnnotatedField = field.getAnnotation(Transient.class) != null;
        
        this.isSynthetic = field.isSynthetic();
    }

    /* (non-Javadoc)
     * @see org.jadira.cloning.portable.FieldModel#getField()
     */
    @Override
    public Field getField() {
        return field;
    }

    /* (non-Javadoc)
     * @see org.jadira.cloning.portable.FieldModel#getFieldType()
     */
    @Override
    public FieldType getFieldType() {
        return fieldType;
    }

    /* (non-Javadoc)
     * @see org.jadira.cloning.portable.FieldModel#getFieldClass()
     */
    @Override
    public Class<?> getFieldClass() {
        return fieldClass;
    }

    /* (non-Javadoc)
     * @see org.jadira.cloning.portable.FieldModel#isTransientField()
     */
    @Override
    public boolean isTransientField() {
        return transientField;
    }
    
    /* (non-Javadoc)
     * @see org.jadira.cloning.portable.FieldModel#isTransientAnnotatedField()
     */
    @Override
    public boolean isTransientAnnotatedField() {
        return transientAnnotatedField;
    }
    
	public boolean isSynthetic() {
		return isSynthetic;
	}
}
