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

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public class JArrayClass extends JClass {

    private Class<?> actualClass;

	protected JArrayClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
        super(JClass.getJClass(Array.class, resolver).getClassFile(), resolver);
        this.actualClass = clazz;
    }

    public static JArrayClass getJClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
        return new JArrayClass(clazz, resolver);
    }
	
    public JClass getSuperClass() throws ClasspathAccessException {

        return null;
    }

    public List<JInterface> getImplementedInterfaces() throws ClasspathAccessException {

        return Collections.emptyList();
    }

    @Override
    public Class<?> getActualClass() throws ClasspathAccessException {

        return this.actualClass;
    }

    public Set<JClass> getSubClasses() {
        return Collections.emptySet();
    }

    public List<JInnerClass> getEnclosedClasses() throws ClasspathAccessException {

        return Collections.emptyList();
    }

    public List<JField> getFields() {

        return Collections.emptyList();
    }

    public List<JConstructor> getConstructors() throws ClasspathAccessException {

        return Collections.emptyList();
    }

    @Override
    public Set<JAnnotation<?>> getAnnotations() {

        return Collections.emptySet();
    }

    @Override
    public JPackage getPackage() throws ClasspathAccessException {

        return null;
    }

    public List<JMethod> getMethods() {

        return Collections.emptyList();
    }

    public List<JStaticInitializer> getStaticInitializers() {

        return Collections.emptyList();
    }
	
	@Override
	public boolean isArray() {
		return true;
	}
	
    public String getName() {
        return actualClass.getName();
    }
    
    public ClassFile getClassFile() {
    	return null;
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
		JArrayClass rhs = (JArrayClass) obj;
		return new EqualsBuilder()
			 	.appendSuper(super.equals(obj))
				.append(actualClass, rhs.actualClass).isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder(11, 47).append(super.hashCode())
				.append(actualClass).toHashCode();
	}
}