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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.core.exception.ClasspathAccessException;
import org.jadira.scanner.core.helper.JavassistMethodInfoHelper;

public abstract class JOperation extends JElement {

    private MethodInfo methodInfo;
    private final JType enclosingType;

    protected JOperation(MethodInfo methodInfo, JType enclosingType, ClasspathResolver resolver) {
        super(methodInfo == null ? null : methodInfo.getName(), resolver);
        this.methodInfo = methodInfo;
        this.enclosingType = enclosingType;
    }

    public JType getEnclosingType() {
        return enclosingType;
    }

    @Override
    public Set<JAnnotation<?>> getAnnotations() {

        AnnotationsAttribute visible = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
        AnnotationsAttribute invisible = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.invisibleTag);

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
    public <A extends java.lang.annotation.Annotation> JAnnotation<A> getAnnotation(Class<A> annotation) {

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
                @SuppressWarnings("unchecked") JAnnotation<A> retVal = (JAnnotation<A>) JAnnotation.getJAnnotation(nextAnnotation, this, getResolver());
                return retVal;
            }
        }

        return null;
    }

    protected MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public List<JParameter> getParameters() throws ClasspathAccessException {

        List<JParameter> params = new ArrayList<JParameter>();
        String[] paramTypes = JavassistMethodInfoHelper.getMethodParamTypeNames(methodInfo);
        for (int i = 0; i < paramTypes.length; i++) {
            params.add(JParameter.getJParameter(i, this, getResolver()));
        }
	    return params;
    }

    public Method getActualMethod() throws ClasspathAccessException {

        try {
            return getEnclosingType().getActualClass().getMethod(getName(), JavassistMethodInfoHelper.getMethodParamClasses(methodInfo));
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Problem obtaining method: " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new ClasspathAccessException("Problem finding method: " + e.getMessage(), e);
        }
    }

    // public List<JLocalVariable> getEnclosedLocalVariables()

    // public List<JAnonymousClass> getEnclosedAnonymousClasses()

    @Override
    public JType getEnclosingElement() {
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
		JOperation rhs = (JOperation) obj;
		return new EqualsBuilder()
			 	.appendSuper(super.equals(obj))
				.append(enclosingType, rhs.enclosingType).isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder(11, 47).append(super.hashCode())
				.append(enclosingType).toHashCode();
	}
}