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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public abstract class JVariable extends JElement {

    protected JVariable(String name, ClasspathResolver resolver) {
        super(name, resolver);
    }

    public abstract JType getEnclosingType() throws ClasspathAccessException;

    public abstract JType getType() throws ClasspathAccessException;

    @Override
    public abstract Set<JAnnotation<?>> getAnnotations() throws ClasspathAccessException;

    @Override
    public <A extends java.lang.annotation.Annotation> JAnnotation<A> getAnnotation(Class<A> annotation) throws ClasspathAccessException {

        Set<JAnnotation<?>> inspAnnotations = getAnnotations();
        for (JAnnotation<?> next : inspAnnotations) {
            if (next.getName().equals(annotation.getName())
                    && (next.getActualAnnotation().getClass().equals(annotation.getClass()))) {
                @SuppressWarnings("unchecked") JAnnotation<A> retVal = (JAnnotation<A>)next;
                return retVal;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append("name", this.getName());
    	builder.append("type", this.getType());
    	builder.append("enclosingType", this.getEnclosingType());
    	
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
		// JVariable rhs = (JVariable) obj;
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