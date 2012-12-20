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

import java.util.Set;

import javassist.bytecode.ClassFile;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.resolver.ClasspathResolver;

public abstract class InspType extends InspElement {

    private final ClassFile classFile;

    protected InspType(ClassFile classFile, ClasspathResolver resolver) {
        super(classFile.getName(), resolver);
        this.classFile = classFile;
    }

    public abstract InspPackage getPackage() throws ClasspathAccessException;

    // public abstract List<InspType> getTypeParams()

    public abstract Class<?> getActualClass() throws ClasspathAccessException;

    @Override
    public <A extends java.lang.annotation.Annotation>InspAnnotation<A> getAnnotation(Class<A> annotation) throws ClasspathAccessException {

        Set<InspAnnotation<?>> inspAnnotations = getAnnotations();
        for (InspAnnotation<?> next : inspAnnotations) {
            if (next.getName().equals(annotation.getName())
                    && (next.getActualAnnotation().annotationType().getClass().equals(annotation.getClass()))) {
                @SuppressWarnings("unchecked") InspAnnotation<A> retVal = (InspAnnotation<A>)next;
                return retVal;
            }
        }
        return null;
    }

    public ClassFile getClassFile() {
        return classFile;
    }
    
    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append("name", getName());
    	
    	return builder.toString();
    }
}