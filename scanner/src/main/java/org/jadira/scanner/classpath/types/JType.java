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

import java.util.Set;

import javassist.bytecode.AccessFlag;
import javassist.bytecode.ClassFile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public abstract class JType extends JElement {

    private final ClassFile classFile;

    protected JType(ClassFile classFile, ClasspathResolver resolver) {
        super(classFile.getName(), resolver);
        this.classFile = classFile;
    }

    public abstract JPackage getPackage() throws ClasspathAccessException;

    // public abstract List<JType> getTypeParams()

    public abstract Class<?> getActualClass() throws ClasspathAccessException;

    
    @Override
    public <A extends java.lang.annotation.Annotation>JAnnotation<A> getAnnotation(Class<A> annotation) throws ClasspathAccessException {

        Set<JAnnotation<?>> inspAnnotations = getAnnotations();
        for (JAnnotation<?> next : inspAnnotations) {
            if (next.getName().equals(annotation.getName())
                    && (next.getActualAnnotation().annotationType().getClass().equals(annotation.getClass()))) {
                @SuppressWarnings("unchecked") JAnnotation<A> retVal = (JAnnotation<A>)next;
                return retVal;
            }
        }
        return null;
    }

    public String getModifier() {
        return isPrivate() ? "private" :
               isProtected() ? "protected" :
               isPublic() ? "public" : "";
    }
    
    public boolean isPublic() {
        return AccessFlag.isPublic(classFile.getAccessFlags());
    }
    
    public boolean isProtected() {
        return AccessFlag.isProtected(classFile.getAccessFlags());
    }
    
    public boolean isPrivate() {
        return AccessFlag.isPrivate(classFile.getAccessFlags());
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
		// JType rhs = (JType) obj;
		return new EqualsBuilder()
			 	.appendSuper(super.equals(obj))
				.isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder(11, 47).append(super.hashCode())
				.toHashCode();
	}
}