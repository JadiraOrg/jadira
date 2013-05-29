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

import java.lang.annotation.Annotation;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public abstract class JElement {
	
    private final String name;
	private final ClasspathResolver resolver;

    protected JElement(String name, ClasspathResolver resolver) {
        this.name = name;
        this.resolver = resolver;
    }

    public String getName() {
        return name;
    }

    public abstract Set<JAnnotation<?>> getAnnotations() throws ClasspathAccessException;

    public abstract <A extends Annotation> JAnnotation<A> getAnnotation(Class<A> annotation) throws ClasspathAccessException;

    public abstract void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException;

    public abstract JElement getEnclosingElement();
    
    protected ClasspathResolver getResolver() {
    		return resolver;
	}
    
	protected static ClassFile findClassFile(final String name, ClasspathResolver resolver) throws ClasspathAccessException {

		final ClassFile retVal;
		if ("boolean".equals(name)) {
			retVal = JClass.findClassFile("java.lang.Boolean", resolver);
		} else if ("byte".equals(name)) {
			retVal = JClass.findClassFile("java.lang.Byte", resolver);
		} else if ("char".equals(name)) {
			retVal = JClass.findClassFile("java.lang.Character", resolver);
		} else if ("short".equals(name)) {
			retVal = JClass.findClassFile("java.lang.Short", resolver);
		} else if ("int".equals(name)) {
			retVal = JClass.findClassFile("java.lang.Integer", resolver);
		} else if ("long".equals(name)) {
			retVal = JClass.findClassFile("java.lang.Long", resolver);
		} else if ("float".equals(name)) {
			retVal = JClass.findClassFile("java.lang.Float", resolver);
		} else if ("double".equals(name)) {
			retVal = JClass.findClassFile("java.lang.Double", resolver);
		} else {
			retVal = resolver.getClassFileResolver().resolveClassFile(name);
		}
		return retVal;
	}
	
    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append(this.name);
    	
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
		JElement rhs = (JElement) obj;
		return new EqualsBuilder()
			.append(name, rhs.name).isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder(11, 47).append(name).toHashCode();
	}
}