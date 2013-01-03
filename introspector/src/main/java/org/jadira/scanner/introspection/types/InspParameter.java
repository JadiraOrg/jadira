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

import java.util.HashSet;
import java.util.Set;

import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.helper.JavassistMethodInfoHelper;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspParameter extends InspVariable {

    private InspOperation enclosingOperation;
    private int index;

    protected InspParameter(int index, InspOperation enclosingOperation, ClasspathResolver resolver) {
        super("" + index, resolver);
        this.enclosingOperation = enclosingOperation;
        this.index = index;
    }

    public static InspParameter getInspParameter(int index, InspOperation enclosingOperation, ClasspathResolver resolver) {
        return new InspParameter(index, enclosingOperation, resolver);
    }

    public InspOperation getEnclosingMethod() {
        return enclosingOperation;
    }

    @Override
    public InspType getEnclosingType() {
        return enclosingOperation.getEnclosingType();
    }

    @Override
    public Set<InspAnnotation<?>> getAnnotations() throws ClasspathAccessException {

        ParameterAnnotationsAttribute paramsVisible = (ParameterAnnotationsAttribute) enclosingOperation.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.visibleTag);
        ParameterAnnotationsAttribute paramsInvisible = (ParameterAnnotationsAttribute) enclosingOperation.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.invisibleTag);

        final Set<InspAnnotation<?>> retVal = new HashSet<InspAnnotation<?>>();
        if (paramsVisible != null && paramsVisible.getAnnotations() != null) {
            for (Annotation anns : paramsVisible.getAnnotations()[index]) {
                retVal.add(InspAnnotation.getInspAnnotation(anns, this, getResolver()));
            }
        }
        if (paramsInvisible != null && paramsInvisible.getAnnotations() != null) {
            for (Annotation anns : paramsInvisible.getAnnotations()[index]) {
                retVal.add(InspAnnotation.getInspAnnotation(anns, this, getResolver()));
            }
        }

        return retVal;
    }

    @Override
    public InspType getType() throws ClasspathAccessException {

        Class<?> clazz;
        if (enclosingOperation instanceof InspConstructor || enclosingOperation instanceof InspMethod) {
            MethodInfo methodInfo = ((InspOperation) enclosingOperation).getMethodInfo();
            String[] paramTypeNames = JavassistMethodInfoHelper.getMethodParamTypeNames(methodInfo);
            try {
				clazz = JavassistMethodInfoHelper.decodeFieldType(paramTypeNames[getIndex()]);
			} catch (ClassNotFoundException e) {
				throw new ClasspathAccessException("Invalid parameter index: " + index, e);
			}

        } else {
            throw new ClasspathAccessException("Invalid parameter index: " + index);
        }

        if (clazz.isAnnotation()) {
            try {
                return new InspAnnotation<java.lang.annotation.Annotation>((java.lang.annotation.Annotation) clazz.newInstance(), this, getResolver());
            } catch (InstantiationException e) {
                throw new ClasspathAccessException("Problem instantiating annotation: " + e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new ClasspathAccessException("Problem accessing annotation: " + e.getMessage(), e);
            }
        } else if (clazz.isInterface()) {
            return new InspInterface(clazz.getName(), getResolver());
        } else {
            InspClass inspClass = new InspClass(clazz.getName(), getResolver());
            return inspClass;
        }
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException {
        visitor.visit(this);

        for (InspAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
    }

    @Override
    public InspOperation getEnclosingElement() {
        return enclosingOperation;
    }
}