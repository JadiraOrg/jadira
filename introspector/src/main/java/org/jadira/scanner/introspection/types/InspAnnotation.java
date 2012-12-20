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

import java.util.HashSet;
import java.util.Set;

import javassist.bytecode.annotation.Annotation;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.helper.JavassistAnnotationsHelper;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspAnnotation<A extends java.lang.annotation.Annotation> extends InspType {

    private InspElement enclosingElement;

    protected InspAnnotation(Annotation annotation, InspElement enclosingElement, ClasspathResolver resolver) {
        super(findClassFile(annotation.getTypeName(), resolver), resolver);
        this.enclosingElement = enclosingElement;
    }

    protected InspAnnotation(java.lang.annotation.Annotation annotation, InspElement enclosingElement, ClasspathResolver resolver) {
        super(findClassFile(annotation.annotationType().getName(), resolver), resolver);
        this.enclosingElement = enclosingElement;
    }

    public static InspAnnotation<?> getInspAnnotation(Annotation nextAnnotation, InspElement enclosingElement, ClasspathResolver resolver) {
        return new InspAnnotation<java.lang.annotation.Annotation>(nextAnnotation, enclosingElement, resolver);
    }

    public static <A extends java.lang.annotation.Annotation> InspAnnotation<A> getInspAnnotation(A nextAnnotation, InspElement enclosingElement, ClasspathResolver resolver) {
        return new InspAnnotation<A>(nextAnnotation, enclosingElement, resolver);
    }

    public A getActualAnnotation() throws ClasspathAccessException {

        final java.lang.annotation.Annotation[] annotations;
        if (enclosingElement instanceof InspOperation) {
            annotations = JavassistAnnotationsHelper.getAnnotationsForMethod(((InspOperation)enclosingElement).getMethodInfo());
        } else if (enclosingElement instanceof InspField) {
            annotations = JavassistAnnotationsHelper.getAnnotationsForFieldInfo(((InspField)enclosingElement).getFieldInfo());
        } else if (enclosingElement instanceof InspParameter) {
            annotations = JavassistAnnotationsHelper.getAnnotationsForMethodParameter(((InspMethod)enclosingElement).getMethodInfo(), ((InspParameter)enclosingElement).getIndex());
        } else if (enclosingElement instanceof InspPackage) {
            annotations = ((InspPackage)enclosingElement).getAnnotationsForPackage();
        } else {
            annotations = JavassistAnnotationsHelper.getAnnotationsForClass(((InspType)enclosingElement).getClassFile());
        }

        String requiredName = getActualClass().getName();
        for (java.lang.annotation.Annotation next : annotations) {
            String nextName = next.annotationType().getName();
            if (nextName.equals(requiredName)) {
                @SuppressWarnings("unchecked") final A retVal = (A) next;
                return retVal;
            }
        }
        throw new ClasspathAccessException("Could not find annotation of type " + getActualClass() + " for " + enclosingElement);
    }

    @Override
    public Set<InspAnnotation<?>> getAnnotations() throws ClasspathAccessException {

        Set<InspAnnotation<?>> retVal = new HashSet<InspAnnotation<?>>();
        java.lang.annotation.Annotation[] anns = JavassistAnnotationsHelper.getAnnotationsForClass(getClassFile());
        for(java.lang.annotation.Annotation next : anns) {
            retVal.add(InspAnnotation.getInspAnnotation(next, this, getResolver()));
        }
        return retVal;
    }

    @Override
    public InspPackage getPackage() throws ClasspathAccessException {
        return InspPackage.getInspPackage(getName().substring(0, getName().lastIndexOf(".")), getResolver());
    }

    @Override
    public Class<A> getActualClass() throws ClasspathAccessException {

        try {
            @SuppressWarnings("unchecked")
            Class<A> retVal = (Class<A>) Class.forName(getName());
            return retVal;
        } catch (ClassNotFoundException e) {
            throw new ClasspathAccessException("Could not find annotation class: " + getName(), e);
        }
    }

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public InspElement getEnclosingElement() {
        return enclosingElement;
    }
    
    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append("name", getName());
    	builder.append("enclosingElement", getEnclosingElement());
    	
    	return builder.toString();
    }

}