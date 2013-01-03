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

import javassist.bytecode.ClassFile;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspPrimitiveClass extends InspClass {

    protected InspPrimitiveClass(String name, ClasspathResolver resolver) throws ClasspathAccessException {
    	this(findClassFile(name, resolver), resolver);
    }

    protected InspPrimitiveClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
        this(findClassFile(clazz.getName(), resolver), resolver);
    }

    protected InspPrimitiveClass(ClassFile classFile, ClasspathResolver resolver) {
        super(classFile, resolver);
    }

    public static InspPrimitiveClass getInspClass(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        return new InspPrimitiveClass(name, resolver);
    }

    public static InspPrimitiveClass getInspClass(ClassFile classFile, ClasspathResolver resolver) {
        return new InspPrimitiveClass(classFile, resolver);
    }

    public static InspPrimitiveClass getInspClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
        return new InspPrimitiveClass(clazz, resolver);
    }

	protected static ClassFile findClassFile(final String name, ClasspathResolver resolver) throws ClasspathAccessException {

		if (("boolean").equals(name)) {
			return InspClass.findClassFile("java.lang.Boolean", resolver);
		} else if (("byte").equals(name)) {
			return InspClass.findClassFile("java.lang.Byte", resolver);
		} else if (("char").equals(name)) {
			return InspClass.findClassFile("java.lang.Character", resolver);
		} else if (("short").equals(name)) {
			return InspClass.findClassFile("java.lang.Short", resolver);
		} else if (("int").equals(name)) {
			return InspClass.findClassFile("java.lang.Integer", resolver);
		} else if (("long").equals(name)) {
			return InspClass.findClassFile("java.lang.Long", resolver);
		} else if (("float").equals(name)) {
			return InspClass.findClassFile("java.lang.Float", resolver);
		} else if (("double").equals(name)) {
			return InspClass.findClassFile("java.lang.Double", resolver);
		} else {
			throw new ClasspathAccessException("Not a valid primitive: " + name);
		}
	}
	
	@Override
	public boolean isPrimitive() {
		return true;
	}
}