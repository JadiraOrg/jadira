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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.InnerClassesAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.filter.JElementTypeFilter;
import org.jadira.scanner.classpath.filter.JTypeSubTypeOfFilter;
import org.jadira.scanner.classpath.projector.ClasspathProjector;
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.api.Projector;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public class JClass extends JType {

    private static final Projector<File> CLASSPATH_PROJECTOR = ClasspathProjector.SINGLETON;
    
    protected JClass(String name, ClasspathResolver resolver) throws ClasspathAccessException {
    	this(findClassFile(name, resolver), resolver);
    }

    protected JClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
        this(findClassFile(clazz.getName(), resolver), resolver);
    }

    protected JClass(ClassFile classFile, ClasspathResolver resolver) {
        super(classFile, resolver);
    }

    public static JClass getJClass(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        return new JClass(name, resolver);
    }

    public static JClass getJClass(ClassFile classFile, ClasspathResolver resolver) {
        return new JClass(classFile, resolver);
    }

    public static JClass getJClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
        return new JClass(clazz, resolver);
    }

    public JClass getSuperType() throws ClasspathAccessException {

        final String superClassFile = getClassFile().getSuperclass();
        return JClass.getJClass(superClassFile, getResolver());
    }
    
    public List<JInterface> getImplementedInterfaces() throws ClasspathAccessException {

        final List<JInterface> retVal = new ArrayList<JInterface>();
        final String[] interfaces = getClassFile().getInterfaces();

        for (String next : interfaces) {
            retVal.add(JInterface.getJInterface(next, getResolver()));
        }
        return retVal;
    }

    @Override
    public Class<?> getActualClass() throws ClasspathAccessException {

        return getResolver().loadClass(getClassFile().getName());
    }

    public Set<JClass> getSubClasses() {
        
        Set<JClass> retVal = new HashSet<JClass>();
        List<? extends ClassFile> classes = getResolver().getClassFileResolver().resolveAll(null, CLASSPATH_PROJECTOR, new JTypeSubTypeOfFilter(this.getActualClass()), new JElementTypeFilter(JClass.class)); 
        for (ClassFile classFile : classes) {
            retVal.add(JClass.getJClass(classFile, getResolver()));
        }
        return retVal;
    }

    public List<JInnerClass> getEnclosedClasses() throws ClasspathAccessException {

        final List<JInnerClass> retVal = new ArrayList<JInnerClass>();
        @SuppressWarnings("unchecked")
        List<AttributeInfo> attrs = (List<AttributeInfo>) getClassFile().getAttributes();
        for (AttributeInfo next : attrs) {
            if (next instanceof InnerClassesAttribute) {
                int innerClassCount = ((InnerClassesAttribute) next).tableLength();
                for (int i = 0; i < innerClassCount; i++) {
                    String innerName = ((InnerClassesAttribute) next).innerClass(i);
                    // Skip anonymous classes - these are returned via method introspection instead
                    if (innerName != null && innerName.startsWith(this.getClassFile().getName())) {
                    	
                    	ClassFile innerClass = findClassFile(innerName, getResolver());

                    	// One or two inner classes can be introspected easily - filter these
                    	if (innerClass != null) { 
                    		retVal.add(JInnerClass.getJInnerClass(this.getClassFile(), innerClass, getResolver()));
                    	}
                    }
                }
            }
        }
        return retVal;
    }

    public List<JField> getFields() {

    	boolean isJavaLangThrowable = "java.lang.Throwable".equals(this.getName());
    	boolean isJavaLangSystem = "java.lang.System".equals(this.getName());
    	
        final List<JField> retVal = new ArrayList<JField>();
        @SuppressWarnings("unchecked")
        final List<FieldInfo> fields = (List<FieldInfo>) getClassFile().getFields();

        for (FieldInfo next : fields) {
        	if (isJavaLangThrowable && ("backtrace".equals(next.getName()))) {
        		// See http://bugs.sun.com/bugdatabase/view_bug.do;jsessionid=d7621f5189c86f127fe5737490903?bug_id=4496456
        		continue;
        	}
        	if (isJavaLangSystem && ("security".equals(next.getName()))) {
                continue;
            }
            retVal.add(JField.getJField(next, this, getResolver()));
        }
        return retVal;
    }

    public List<JConstructor> getConstructors() throws ClasspathAccessException {

        final List<JConstructor> retVal = new ArrayList<JConstructor>();
        @SuppressWarnings("unchecked")
        final List<MethodInfo> methods = (List<MethodInfo>) getClassFile().getMethods();

        for (MethodInfo next : methods) {
            if (next.isConstructor()) {
                retVal.add(JConstructor.getJConstructor(next, this, getResolver()));
            }
        }
        
        if (retVal.isEmpty()) {
        	retVal.add(JDefaultConstructor.getJConstructor((MethodInfo)null, (JClass)this, getResolver()));
        }
        	
        return retVal;
    }

    @Override
    public Set<JAnnotation<?>> getAnnotations() {

        AnnotationsAttribute visible = (AnnotationsAttribute) getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        AnnotationsAttribute invisible = (AnnotationsAttribute) getClassFile().getAttribute(AnnotationsAttribute.invisibleTag);

        Set<JAnnotation<?>> annotations = new HashSet<JAnnotation<?>>();

        List<Annotation> annotationsList = new ArrayList<Annotation>();
        if (visible != null) {
            annotationsList.addAll(Arrays.asList(visible.getAnnotations()));
        }
        if (invisible != null) {
            annotationsList.addAll(Arrays.asList(invisible.getAnnotations()));
        }

        for (Annotation nextAnnotation : annotationsList) {
            annotations.add(JAnnotation.getJAnnotation(nextAnnotation, this, getResolver()));
        }

        return annotations;
    }

    @Override
    public JPackage getPackage() throws ClasspathAccessException {

        String fqClassName = getClassFile().getName();

        String packageName;
        if (fqClassName.contains(".")) {
            packageName = fqClassName.substring(0, fqClassName.lastIndexOf('.'));
        } else {
            packageName = "";
        }

        return JPackage.getJPackage(packageName, getResolver());
    }

    public List<JMethod> getMethods() {

        final List<JMethod> retVal = new ArrayList<JMethod>();
        @SuppressWarnings("unchecked")
        final List<MethodInfo> methods = (List<MethodInfo>) getClassFile().getMethods();

        for (MethodInfo next : methods) {
            if (next.isMethod()) {
                retVal.add(JMethod.getJMethod(next, this, getResolver()));
            }
        }
        return retVal;
    }

    public List<JStaticInitializer> getStaticInitializers() {

        final List<JStaticInitializer> retVal = new ArrayList<JStaticInitializer>();
        @SuppressWarnings("unchecked")
        final List<MethodInfo> methods = (List<MethodInfo>) getClassFile().getMethods();

        for (MethodInfo next : methods) {
            if (next.isStaticInitializer()) {
                retVal.add(JStaticInitializer.getJStaticInitializer(next, this, getResolver()));
            }
        }
        return retVal;
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
    public JElement getEnclosingElement() {
        return getPackage();
    }

	public boolean isPrimitive() {
		return false;
	}
	
	public boolean isArray() {
		return false;
	}
	
	public boolean isInterface() {
		return getClassFile().isInterface();
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
		// JClass rhs = (JClass) obj;
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