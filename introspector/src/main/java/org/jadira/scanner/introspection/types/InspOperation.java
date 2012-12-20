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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.helper.JavassistMethodInfoHelper;
import org.jadira.scanner.resolver.ClasspathResolver;

public abstract class InspOperation extends InspElement {

    private MethodInfo methodInfo;
    private final InspType enclosingType;

    protected InspOperation(MethodInfo methodInfo, InspType enclosingType, ClasspathResolver resolver) {
        super(methodInfo.getName(), resolver);
        this.methodInfo = methodInfo;
        this.enclosingType = enclosingType;
    }

    public InspType getEnclosingType() {
        return enclosingType;
    }

    @Override
    public Set<InspAnnotation<?>> getAnnotations() {

        AnnotationsAttribute visible = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
        AnnotationsAttribute invisible = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.invisibleTag);

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
    public <A extends java.lang.annotation.Annotation> InspAnnotation<A> getAnnotation(Class<A> annotation) {

        AnnotationsAttribute visible = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
        AnnotationsAttribute invisible = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.invisibleTag);

        List<javassist.bytecode.annotation.Annotation> annotationsList = new ArrayList<Annotation>();
        if (visible != null) {
            annotationsList.addAll(Arrays.asList(visible.getAnnotations()));
        }
        if (invisible != null) {
            annotationsList.addAll(Arrays.asList(invisible.getAnnotations()));
        }

        for (javassist.bytecode.annotation.Annotation nextAnnotation : annotationsList) {
            if (annotation.getName().equals(nextAnnotation.getTypeName())) {
                @SuppressWarnings("unchecked") InspAnnotation<A> retVal = (InspAnnotation<A>) InspAnnotation.getInspAnnotation(nextAnnotation, this, getResolver());
                return retVal;
            }
        }

        return null;
    }

    protected MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public List<InspParameter> getParameters() throws ClasspathAccessException {

        List<InspParameter> params = new ArrayList<InspParameter>();
        Class<?>[] paramClasses = JavassistMethodInfoHelper.getMethodParams(methodInfo);
        for (int i = 0; i < paramClasses.length; i++) {
            params.add(InspParameter.getInspParameter(i, this, getResolver()));
        }
        return params;
    }

    public Method getActualMethod() throws ClasspathAccessException {

        try {
            return getEnclosingType().getActualClass().getMethod(getName(), JavassistMethodInfoHelper.getMethodParams(methodInfo));
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Problem obtaining method: " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new ClasspathAccessException("Problem finding method: " + e.getMessage(), e);
        }
    }

    // public List<InspLocalVariable> getEnclosedLocalVariables()

    // public List<InspAnonymousClass> getEnclosedAnonymousClasses()

    @Override
    public InspType getEnclosingElement() {
        return enclosingType;
    }
    
    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append("name",this.getName());
    	builder.append("enclosingType",this.getEnclosingType());
    	builder.append("parameters",this.getParameters());
    	
    	return builder.toString();
    }
}