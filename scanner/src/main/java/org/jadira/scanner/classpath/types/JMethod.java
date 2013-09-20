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
import java.util.List;

import javassist.bytecode.AccessFlag;
import javassist.bytecode.MethodInfo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public class JMethod extends JOperation {

    protected JMethod(MethodInfo methodInfo, JType enclosingType, ClasspathResolver resolver) {
        super(methodInfo, enclosingType, resolver);
    }

    public static JMethod getJMethod(MethodInfo methodInfo, JType enclosingType, ClasspathResolver resolver) {
        return new JMethod(methodInfo, enclosingType, resolver);
    }
    
    @Override
    public Method getActualMethod() throws ClasspathAccessException {

        List<JParameter> params = getParameters();
        Class<?>[] paramClasses = new Class<?>[params.size()];
        for (int i = 0; i < params.size(); i++) {
            paramClasses[i] = params.get(i).getType().getActualClass();
        }

        try {
            return getEnclosingType().getActualClass().getDeclaredMethod(getName(), paramClasses);
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Could not access class: " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new ClasspathAccessException("Could not find method: " + e.getMessage(), e);
        }
    }

    public String getModifier() {
        return isPrivate() ? "private" :
               isProtected() ? "protected" :
               isPublic() ? "public" : "";
    }
    
    public boolean isPublic() {
        return AccessFlag.isPublic(getMethodInfo().getAccessFlags());
    }
    
    public boolean isProtected() {
        return AccessFlag.isProtected(getMethodInfo().getAccessFlags());
    }
     
    public boolean isPrivate() {
        return AccessFlag.isPrivate(getMethodInfo().getAccessFlags());
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
		// JMethod rhs = (JMethod) obj;
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