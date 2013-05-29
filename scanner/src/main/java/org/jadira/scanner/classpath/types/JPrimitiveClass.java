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

import javassist.bytecode.ClassFile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public class JPrimitiveClass extends JClass {

    protected JPrimitiveClass(String name, ClasspathResolver resolver) throws ClasspathAccessException {
    	super(findClassFile(name, resolver), resolver);
    }

    protected JPrimitiveClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
    	super(findClassFile(clazz.getName(), resolver), resolver);
    }

    protected JPrimitiveClass(ClassFile classFile, ClasspathResolver resolver) {
        super(classFile, resolver);
    }

    public static JPrimitiveClass getJClass(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        return new JPrimitiveClass(name, resolver);
    }

    public static JPrimitiveClass getJClass(ClassFile classFile, ClasspathResolver resolver) {
        return new JPrimitiveClass(classFile, resolver);
    }

    public static JPrimitiveClass getJClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
        return new JPrimitiveClass(clazz, resolver);
    }

	protected static final ClassFile findClassFile(final String name, ClasspathResolver resolver) throws ClasspathAccessException {

		if ("boolean".equals(name)) {
			return JClass.findClassFile("java.lang.Boolean", resolver);
		} else if ("byte".equals(name)) {
			return JClass.findClassFile("java.lang.Byte", resolver);
		} else if ("char".equals(name)) {
			return JClass.findClassFile("java.lang.Character", resolver);
		} else if ("short".equals(name)) {
			return JClass.findClassFile("java.lang.Short", resolver);
		} else if ("int".equals(name)) {
			return JClass.findClassFile("java.lang.Integer", resolver);
		} else if ("long".equals(name)) {
			return JClass.findClassFile("java.lang.Long", resolver);
		} else if ("float".equals(name)) {
			return JClass.findClassFile("java.lang.Float", resolver);
		} else if ("double".equals(name)) {
			return JClass.findClassFile("java.lang.Double", resolver);
		} else {
			throw new ClasspathAccessException("Not a valid primitive: " + name);
		}
	}
	
	@Override
	public boolean isPrimitive() {
		return true;
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
		// JPrimitiveClass rhs = (JPrimitiveClass) obj;
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