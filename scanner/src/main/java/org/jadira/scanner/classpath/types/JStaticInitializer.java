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

import javassist.bytecode.MethodInfo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public class JStaticInitializer extends JOperation {

    protected JStaticInitializer(MethodInfo methodInfo, JType enclosingType, ClasspathResolver resolver) {
        super(methodInfo, enclosingType, resolver);
    }

    public static JStaticInitializer getJStaticInitializer(MethodInfo methodInfo, JType enclosingType, ClasspathResolver resolver) {
        return new JStaticInitializer(methodInfo, enclosingType, resolver);
    }

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException {
        visitor.visit(this);

        for (JParameter next : getParameters()) {
            next.acceptVisitor(visitor);
        }
        for (JAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
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
		// JStaticInitializer rhs = (JStaticInitializer) obj;
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