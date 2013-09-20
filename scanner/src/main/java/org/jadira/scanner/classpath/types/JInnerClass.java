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

import java.util.Collections;
import java.util.List;

import javassist.bytecode.ClassFile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public class JInnerClass extends JClass {

    private ClassFile enclosingClass;

    protected JInnerClass(ClassFile enclosingClass, ClassFile classFile, ClasspathResolver resolver) throws ClasspathAccessException {
        super(classFile, resolver);
        // Get the inner class definition
        this.enclosingClass = enclosingClass;
    }

    public static JInnerClass getJInnerClass(ClassFile enclosingClass, ClassFile classFile, ClasspathResolver resolver) throws ClasspathAccessException {
        return new JInnerClass(enclosingClass, classFile, resolver);
    }

    public JClass getEnclosingClass() {
        return JClass.getJClass(enclosingClass, getResolver());
    }

    @Override
    public JClass getEnclosingElement() {
        return getEnclosingClass();
    }
     
    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException {
        visitor.visit(this);

        for (JInterface next : getImplementedInterfaces()) {
            next.acceptVisitor(visitor);
        }
        for (JInnerClass next : getEnclosedClasses()) {
            next.acceptVisitor(visitor);
        }
        for (JField next : getFields()) {
            next.acceptVisitor(visitor);
        }
        for (JConstructor next : getConstructors()) {
            next.acceptVisitor(visitor);
        }
        for (JMethod next : getMethods()) {
            next.acceptVisitor(visitor);
        }
        for (JStaticInitializer next : getStaticInitializers()) {
            next.acceptVisitor(visitor);
        }
        for (JAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
    }
    
    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append("name", getName());
    	builder.append("enclosingClass", getEnclosingClass());
    	
    	return builder.toString();
    }
    
    @Override
    public List<JInnerClass> getEnclosedClasses() {
    	return Collections.emptyList();
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
		JInnerClass rhs = (JInnerClass) obj;
		return new EqualsBuilder()
			 	.appendSuper(super.equals(obj))
				.append(enclosingClass, rhs.enclosingClass).isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder(11, 47).append(super.hashCode())
				.append(enclosingClass).toHashCode();
	}
}