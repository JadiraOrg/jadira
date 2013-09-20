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
package org.jadira.scanner.classpath.types;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public class JField extends JVariable {

    private FieldInfo fieldInfo;
    private JClass jClass;

    protected JField(FieldInfo fieldInfo, JClass jClass, ClasspathResolver resolver) {
        super(fieldInfo.getName(), resolver);
        this.fieldInfo = fieldInfo;
        this.jClass = jClass;
    }

    public static JField getJField(FieldInfo fieldInfo, JClass jClass, ClasspathResolver resolver) {
        return new JField(fieldInfo, jClass, resolver);
    }

    @Override
    public JType getEnclosingType() {
        return jClass;
    }

    @Override
    public JType getType() throws ClasspathAccessException {

        final JType retVal;

        Class<?> clazz;
        try {
            Field field = getActualField();
            clazz = field.getType();            
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Problem finding enclosing type: " + e.getMessage(), e);
        }
        if (clazz.isInterface()) {
            retVal = JInterface.getJInterface(clazz.getName(), getResolver());
        } else if (clazz.isPrimitive()) {
        	retVal = JPrimitiveClass.getJClass(clazz.getName(), getResolver());
        } else if (clazz.isArray()) {
        	retVal = JArrayClass.getJClass(clazz, getResolver());
        } else {
            retVal = JClass.getJClass(clazz.getName(), getResolver());
        }
        return retVal;
    }
    
	public Class<?> decodeFieldType(String componentType) throws ClassNotFoundException {

		char type = componentType.charAt(0);
		String fieldContent = componentType.substring(1);

		switch (type) {
		// L<classname>; reference an instance of class <classname>		
		case 'L': 
			return getResolver().loadClass(fieldContent.replace('/', '.'));
		// B byte signed byte
		case 'B': 
			return Byte.class;
		// C char Unicode character
		case 'C': 
			return Character.class;
		// D double double-precision floating-point value
		case 'D': 
			return Double.class;
		// F float single-precision floating-point value
		case 'F': 
			return Float.class;
		// I int integer
		case 'I': 
			return Integer.class;
		// J long long integer
		case 'J': 
			return Long.class;
		// S short signed short
		case 'S': 
			return Short.class;
		// Z boolean true or false
		case 'Z': 
			return Boolean.class;
		// [ reference one array dimension
		case '[': 
		    return getResolver().loadClass(componentType.replace('/', '.') + ";");
		}
		return null;
	}

    @Override
    public Set<JAnnotation<?>> getAnnotations() {

        AnnotationsAttribute visible = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);
        AnnotationsAttribute invisible = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.invisibleTag);

        Set<JAnnotation<?>> annotations = new HashSet<JAnnotation<?>>();

        List<Annotation> annotationsList = new ArrayList<Annotation>();
        if (visible != null) {
            annotationsList.addAll(Arrays.asList(visible.getAnnotations()));
        }
        if (invisible != null) {
            annotationsList.addAll(Arrays.asList(invisible.getAnnotations()));
        }

        for (Annotation nextAnnotation : annotationsList) {
            annotations.add(JAnnotation.getJAnnotation(nextAnnotation, this, getResolver()));
        }

        return annotations;
    }

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) {
        visitor.visit(this);

        for (JAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
    }

    public Field getActualField() {

        try {
            return getEnclosingType().getActualClass().getDeclaredField(getName());
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Problem obtaining field: " + e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            throw new ClasspathAccessException("Problem finding field: " + this.toString(), e);
        }
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    @Override
    public JClass getEnclosingElement() {
        return jClass;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		JField rhs = (JField) obj;
		return new EqualsBuilder()
			 	.appendSuper(super.equals(obj))
				.append(jClass, rhs.jClass).isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder(11, 47).append(super.hashCode())
				.append(jClass.getName()).toHashCode();
	}
}