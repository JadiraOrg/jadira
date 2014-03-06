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
package org.jadira.reflection.access.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

import org.jadira.reflection.access.api.FieldAccess;
import org.jadira.reflection.cloning.annotation.Transient;

/**
 * Provides a model resulting from introspection of a field of a class suitable for use with Java Reflection
 */
public class FieldModel<C> {
	
	private static final ConcurrentHashMap<String, FieldModel<?>> fieldModels = new ConcurrentHashMap<String, FieldModel<?>>(100);
    
	private static final Object MONITOR = new Object();
	
	private final FieldAccess<C> fieldAccess;
	
    private final Field field;
    private final FieldType fieldType;
    private final Class<?> fieldClass;

    private final boolean transientField;
    private final boolean transientAnnotatedField;
	
    private final boolean isSynthetic;
    
    private final boolean isPrivate;
    
    private FieldModel(Field field, FieldAccess<C> fieldAccess) {
        
    	this.fieldAccess = fieldAccess;
    	
    	this.isPrivate = Modifier.isPrivate(field.getModifiers());
    	
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
    
    /**
     * Returns a field model for the given Field and FieldAccess instance. If a FieldModel 
     * already exists, it will be reused.
     * @param f The Field
     * @param fieldAccess The Field Access that can be used to introspect the field
     * @param <C> The type of class being accessed
     * @return The Field Model
     */
    @SuppressWarnings("unchecked")
	public static final <C> FieldModel<C> get(Field f, FieldAccess<C> fieldAccess) {
		
		String fieldModelKey = (fieldAccess.getClass().getSimpleName() + ":" + f.getClass().getName() + "#" + f.getName());		
		FieldModel<C> fieldModel = (FieldModel<C>)fieldModels.get(fieldModelKey);
    	if (fieldModel != null) {       	
        	return fieldModel;
        }
    	
    	synchronized(MONITOR) {
    		fieldModel = (FieldModel<C>)fieldModels.get(fieldModelKey);
        	if (fieldModel != null) {       	
            	return fieldModel;
            } else {
            	fieldModel = new FieldModel<C>(f, fieldAccess);
            	fieldModels.putIfAbsent(fieldModelKey, fieldModel);
            	
            	return fieldModel;
            }
    	}
    }
    
    /**
     * Access the FieldAccess associated with the FieldModel
     * @return The associated FieldAccess.
     */
	public FieldAccess<C> getFieldAccess() {
		return fieldAccess;
	}
	
	/**
	 * Access the Field associated with the FieldModel
	 * @return The associated Field
	 */
    public Field getField() {
        return field;
    }

    /**
     * Indicates the type of the Field - Primitive, Array or Object
     * @return The FieldType
     */
    public FieldType getFieldType() {
        return fieldType;
    }

    /**
     * Gets the Declared Class for the Field
     * @return The Class for the Field
     */
    public Class<?> getFieldClass() {
        return fieldClass;
    }

    /**
     * Indicates whether the field is transient
     * @return True if transient
     */
    public boolean isTransientField() {
        return transientField;
    }

    /**
     * Indicates whether the field carries a Transient annotation
     * @return True if transient annotated
     */
    public boolean isTransientAnnotatedField() {
        return transientAnnotatedField;
    }
    
    /**
     * Indicates whether the field is synthetic according to the Java Language Specific
     * @return True if synthetic
     */
	public boolean isSynthetic() {
		return isSynthetic;
	}
	
    /**
     * Indicates whether the field is private access
     * @return True if private
     */
	public boolean isPrivate() {
		return isPrivate;
	}
}
