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

import java.util.HashSet;
import java.util.Set;

import javassist.bytecode.annotation.Annotation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.exception.ClasspathAccessException;
import org.jadira.scanner.core.helper.JavassistAnnotationsHelper;

public class JAnnotation<A extends java.lang.annotation.Annotation> extends JType {

    private JElement enclosingElement;

    protected JAnnotation(Annotation annotation, JElement enclosingElement, ClasspathResolver resolver) {
        super(findClassFile(annotation.getTypeName(), resolver), resolver);
        this.enclosingElement = enclosingElement;
    }

    protected JAnnotation(java.lang.annotation.Annotation annotation, JElement enclosingElement, ClasspathResolver resolver) {
        super(findClassFile(annotation.annotationType().getName(), resolver), resolver);
        this.enclosingElement = enclosingElement;
    }
    
    public JClass getSuperType() throws ClasspathAccessException {

        final String superClassFile = getClassFile().getSuperclass();
        return JClass.getJClass(superClassFile, getResolver());
    }
    
    public Class<?> getActualSuperType() throws ClasspathAccessException {
        return getSuperType().getActualClass();
    }

    public static JAnnotation<?> getJAnnotation(Annotation nextAnnotation, JElement enclosingElement, ClasspathResolver resolver) {
        return new JAnnotation<java.lang.annotation.Annotation>(nextAnnotation, enclosingElement, resolver);
    }

    public static <A extends java.lang.annotation.Annotation> JAnnotation<A> getJAnnotation(A nextAnnotation, JElement enclosingElement, ClasspathResolver resolver) {
        return new JAnnotation<A>(nextAnnotation, enclosingElement, resolver);
    }

    public A getActualAnnotation() throws ClasspathAccessException {

        final java.lang.annotation.Annotation[] annotations;
        if (enclosingElement instanceof JOperation) {
            annotations = JavassistAnnotationsHelper.getAnnotationsForMethod(((JOperation)enclosingElement).getMethodInfo());
        } else if (enclosingElement instanceof JField) {
            annotations = JavassistAnnotationsHelper.getAnnotationsForFieldInfo(((JField)enclosingElement).getFieldInfo());
        } else if (enclosingElement instanceof JParameter) {
            annotations = JavassistAnnotationsHelper.getAnnotationsForMethodParameter(((JMethod)enclosingElement).getMethodInfo(), ((JParameter)enclosingElement).getIndex());
        } else if (enclosingElement instanceof JPackage) {
            annotations = ((JPackage)enclosingElement).getAnnotationsForPackage();
        } else {
            annotations = JavassistAnnotationsHelper.getAnnotationsForClass(((JType)enclosingElement).getClassFile());
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
    public Set<JAnnotation<?>> getAnnotations() throws ClasspathAccessException {

        Set<JAnnotation<?>> retVal = new HashSet<JAnnotation<?>>();
        java.lang.annotation.Annotation[] anns = JavassistAnnotationsHelper.getAnnotationsForClass(getClassFile());
        for(java.lang.annotation.Annotation next : anns) {
            retVal.add(JAnnotation.getJAnnotation(next, this, getResolver()));
        }
        return retVal;
    }

    @Override
    public JPackage getPackage() throws ClasspathAccessException {
        return JPackage.getJPackage(getName().substring(0, getName().lastIndexOf(".")), getResolver());
    }

    @Override
    public Class<A> getActualClass() throws ClasspathAccessException {

        @SuppressWarnings("unchecked")
        Class<A> retVal = (Class<A>) getResolver().loadClass(getClassFile().getName());
        return retVal;
    }

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public JElement getEnclosingElement() {
        return enclosingElement;
    }
    
    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append("name", getName());
    	builder.append("enclosingElement", getEnclosingElement());
    	
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
		JAnnotation<?> rhs = (JAnnotation<?>) obj;
		return new EqualsBuilder()
			 	.appendSuper(super.equals(obj))
				.append(enclosingElement, rhs.enclosingElement).isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder(11, 47).append(super.hashCode())
				.append(enclosingElement).toHashCode();
	}
}