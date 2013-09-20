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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.exception.ClasspathAccessException;
import org.jadira.scanner.core.helper.JavassistMethodInfoHelper;

public class JParameter extends JVariable {

    private JOperation enclosingOperation;
    private int index;

    protected JParameter(int index, JOperation enclosingOperation, ClasspathResolver resolver) {
        super(JavassistMethodInfoHelper.getMethodParamNames(enclosingOperation.getMethodInfo())[index], resolver);
        this.enclosingOperation = enclosingOperation;
        this.index = index;
    }

    public static JParameter getJParameter(int index, JOperation enclosingOperation, ClasspathResolver resolver) {
        return new JParameter(index, enclosingOperation, resolver);
    }

    public JOperation getEnclosingMethod() {
        return enclosingOperation;
    }

    @Override
    public JType getEnclosingType() {
        return enclosingOperation.getEnclosingType();
    }

    @Override
    public Set<JAnnotation<?>> getAnnotations() throws ClasspathAccessException {

        ParameterAnnotationsAttribute paramsVisible = (ParameterAnnotationsAttribute) enclosingOperation.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.visibleTag);
        ParameterAnnotationsAttribute paramsInvisible = (ParameterAnnotationsAttribute) enclosingOperation.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.invisibleTag);

        final Set<JAnnotation<?>> retVal = new HashSet<JAnnotation<?>>();
        if (paramsVisible != null && paramsVisible.getAnnotations() != null) {
            for (Annotation anns : paramsVisible.getAnnotations()[index]) {
                retVal.add(JAnnotation.getJAnnotation(anns, this, getResolver()));
            }
        }
        if (paramsInvisible != null && paramsInvisible.getAnnotations() != null) {
            for (Annotation anns : paramsInvisible.getAnnotations()[index]) {
                retVal.add(JAnnotation.getJAnnotation(anns, this, getResolver()));
            }
        }

        return retVal;
    }

    @Override
    public JType getType() throws ClasspathAccessException {

        Class<?> clazz;
        if (enclosingOperation instanceof JConstructor || enclosingOperation instanceof JMethod) {
            MethodInfo methodInfo = ((JOperation) enclosingOperation).getMethodInfo();
            String[] paramTypeNames = JavassistMethodInfoHelper.getMethodParamTypeNames(methodInfo);
            clazz = decodeFieldType(paramTypeNames[getIndex()]);

        } else {
            throw new ClasspathAccessException("Invalid parameter index: " + index);
        }

        if (clazz.isAnnotation()) {
            try {
                return new JAnnotation<java.lang.annotation.Annotation>((java.lang.annotation.Annotation) clazz.newInstance(), this, getResolver());
            } catch (InstantiationException e) {
                throw new ClasspathAccessException("Problem instantiating annotation: " + e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new ClasspathAccessException("Problem accessing annotation: " + e.getMessage(), e);
            }
        } else if (clazz.isInterface()) {
            return new JInterface(clazz.getName(), getResolver());
        } else {
            JClass jClass = new JClass(clazz.getName(), getResolver());
            return jClass;
        }
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException {
        visitor.visit(this);

        for (JAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
    }

    @Override
    public JOperation getEnclosingElement() {
        return enclosingOperation;
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
		JParameter rhs = (JParameter) obj;
		return new EqualsBuilder()
			 	.appendSuper(super.equals(obj))
				.append(enclosingOperation, rhs.enclosingOperation).isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder(11, 47).append(super.hashCode())
				.append(enclosingOperation).toHashCode();
	}
    

    private Class<?> decodeFieldType(String componentType) {

        char type = componentType.charAt(0);
        String fieldContent = componentType.substring(1);

        switch (type) {
        // L<classname>; reference an instance of class <classname>
        case 'L': 
            return getResolver().loadClass(fieldContent.replace('/', '.'));
        // B byte signed byte
        case 'B': 
            return Byte.class;
        // C char Unicode character
        case 'C': 
            return Character.class;
        // D double double-precision floating-point value
        case 'D': 
            return Double.class;
        // F float single-precision floating-point value        
        case 'F': 
            return Float.class;
        // I int integer
        case 'I': 
            return Integer.class;
        // J long long integer
        case 'J': 
            return Long.class;
        // S short signed short
        case 'S': 
            return Short.class;
        // Z boolean true or false
        case 'Z': 
            return Boolean.class;
        // [ reference one array dimension
        case '[': 
            return Arrays.class;
        }
        return null;
    }
}