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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.AccessFlag;
import javassist.bytecode.MethodInfo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public class JConstructor extends JOperation {

    protected JConstructor(MethodInfo methodInfo, JClass jClass, ClasspathResolver resolver) {
        super(methodInfo, jClass, resolver);
    }

    public static JConstructor getJConstructor(MethodInfo methodInfo, JClass jClass, ClasspathResolver resolver) {
        return new JConstructor(methodInfo, jClass, resolver);
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
    
    public Constructor<?> getActualConstructor() throws ClasspathAccessException {

        Class<?>[] methodParams = getMethodParamClasses(getMethodInfo());

        try {
            Class<?> clazz = ((JClass) getEnclosingType()).getActualClass();
            return clazz.getDeclaredConstructor(methodParams);
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Could not access constructor: " + e, e);
        } catch (NoSuchMethodException e) {
            throw new ClasspathAccessException("Could not find constructor: " + e, e);
        }
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
    public JClass getEnclosingElement() {
        return (JClass)super.getEnclosingElement();
    }
    
    @Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(getClass().equals(obj.getClass()))) {
			return false;
		}
		// JConstructor rhs = (JConstructor) obj;
		return new EqualsBuilder()
			 	.appendSuper(super.equals(obj))
				.isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder(11, 47).append(super.hashCode())
				.toHashCode();
	}
    
    @Override
    public Set<JAnnotation<?>> getAnnotations() {

        if (getMethodInfo() == null) {
        	return new HashSet<JAnnotation<?>>();
        } else {
        	return super.getAnnotations();
        }    
    }

    @Override
    public <A extends java.lang.annotation.Annotation> JAnnotation<A> getAnnotation(Class<A> annotation) {

        if (getMethodInfo() == null) {
        	return null;
        } else {
        	return super.getAnnotation(annotation);
        }    
    }

    public List<JParameter> getParameters() throws ClasspathAccessException {

        if (getMethodInfo() == null) {
        	List<JParameter> params = new ArrayList<JParameter>();
        	return params;
        } else {
        	return super.getParameters();
        }
    }

    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append("name",this.getName());
    	builder.append("enclosingType",this.getEnclosingType());
    	builder.append("parameters",this.getParameters());
    	
    	return builder.toString();
    }
}