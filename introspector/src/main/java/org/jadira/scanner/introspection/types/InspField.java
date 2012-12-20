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
            clazz = getEnclosingType().getClass().getField(fieldInfo.getName()).getType();
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Problem finding enclosing type: " + e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            throw new ClasspathAccessException("Problem finding requested field: " + e.getMessage(), e);        }
        if (clazz.isEnum()) {
            retVal = InspEnum.getInspEnum(clazz.getName(), getResolver());
        } else if (clazz.isInterface()) {
            retVal = InspInterface.getInspInterface(clazz.getName(), getResolver());
        } else {
            retVal = InspClass.getInspClass(clazz.getName(), getResolver());
        }
        return retVal;
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
            return getEnclosingType().getActualClass().getField(getName());
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