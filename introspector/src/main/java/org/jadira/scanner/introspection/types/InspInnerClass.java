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

import java.util.Collections;
import java.util.List;

import javassist.bytecode.ClassFile;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspInnerClass extends InspClass {

    private ClassFile enclosingClass;

    protected InspInnerClass(ClassFile enclosingClass, ClassFile classFile, ClasspathResolver resolver) throws ClasspathAccessException {
        super(classFile, resolver);
        // Get the inner class definition
        this.enclosingClass = enclosingClass;
    }

    public static InspInnerClass getInspInnerClass(ClassFile enclosingClass, ClassFile classFile, ClasspathResolver resolver) throws ClasspathAccessException {
        return new InspInnerClass(enclosingClass, classFile, resolver);
    }

    public InspClass getEnclosingClass() {
        return InspClass.getInspClass(enclosingClass, getResolver());
    }

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException {
        super.acceptVisitor(visitor);
    }

    @Override
    public InspClass getEnclosingElement() {
        return getEnclosingClass();
    }
     
    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append("name", getName());
    	builder.append("enclosingClass", getEnclosingClass());
    	
    	return builder.toString();
    }
    
    @Override
    public List<InspInnerClass> getEnclosedClasses() {
    	return Collections.emptyList();
    }
}