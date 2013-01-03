/*
 *  Copyright 2012 Chris Pheby
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
package org.jadira.scanner.introspection.types;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspField extends InspVariable {

    private FieldInfo fieldInfo;
    private InspClass inspClass;

    protected InspField(FieldInfo fieldInfo, InspClass inspClass, ClasspathResolver resolver) {
        super(fieldInfo.getName(), resolver);
        this.fieldInfo = fieldInfo;
        this.inspClass = inspClass;
    }

    public static InspField getInspField(FieldInfo fieldInfo, InspClass inspClass, ClasspathResolver resolver) {
        return new InspField(fieldInfo, inspClass, resolver);
    }

    @Override
    public InspType getEnclosingType() {
        return inspClass;
    }

    @Override
    public InspType getType() throws ClasspathAccessException {

        final InspType retVal;

        Class<?> clazz;
        try {
            Field field = getActualField();
            clazz = field.getType();            
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Problem finding enclosing type: " + e.getMessage(), e);
        }
        if (clazz.isInterface()) {
            retVal = InspInterface.getInspInterface(clazz.getName(), getResolver());
        } else if (clazz.isPrimitive()) {
        	retVal = InspPrimitiveClass.getInspClass(clazz.getName(), getResolver());
        } else if (clazz.isArray()) {
        	retVal = InspArrayClass.getInspClass(clazz, getResolver());
        } else {
            retVal = InspClass.getInspClass(clazz.getName(), getResolver());
        }
        return retVal;
    }
    
	public static Class<?> decodeFieldType(String componentType) throws ClassNotFoundException {

		char type = componentType.charAt(0);
		String fieldContent = componentType.substring(1);

		switch (type) {
		case 'L': // L<classname>; reference an instance of class <classname>
			return Class.forName(fieldContent.replace('/', '.'));
		case 'B': // B byte signed byte
			return Byte.class;
		case 'C': // C char Unicode character
			return Character.class;
		case 'D': // D double double-precision floating-point value
			return Double.class;
		case 'F': // F float single-precision floating-point value
			return Float.class;
		case 'I': // I int integer
			return Integer.class;
		case 'J': // J long long integer
			return Long.class;
		case 'S': // S short signed short
			return Short.class;
		case 'Z': // Z boolean true or false
			return Boolean.class;
		case '[': // [ reference one array dimension
			return Class.forName(componentType.replace('/', '.') + ";");
		}
		return null;
	}

    @Override
    public Set<InspAnnotation<?>> getAnnotations() {

        AnnotationsAttribute visible = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);
        AnnotationsAttribute invisible = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.invisibleTag);

        Set<InspAnnotation<?>> annotations = new HashSet<InspAnnotation<?>>();

        List<Annotation> annotationsList = new ArrayList<Annotation>();
        if (visible != null) {
            annotationsList.addAll(Arrays.asList(visible.getAnnotations()));
        }
        if (invisible != null) {
            annotationsList.addAll(Arrays.asList(invisible.getAnnotations()));
        }

        for (Annotation nextAnnotation : annotationsList) {
            annotations.add(InspAnnotation.getInspAnnotation(nextAnnotation, this, getResolver()));
        }

        return annotations;
    }

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) {
        visitor.visit(this);

        for (InspAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
    }

    public Field getActualField() {

        try {
            return getEnclosingType().getActualClass().getDeclaredField(getName());
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Problem obtaining field: " + e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            throw new ClasspathAccessException("Problem finding field: " + e.getMessage(), e);
        }
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    @Override
    public InspClass getEnclosingElement() {
        return inspClass;
    }
}