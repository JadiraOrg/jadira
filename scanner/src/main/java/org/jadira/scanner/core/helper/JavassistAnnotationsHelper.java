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
package org.jadira.scanner.core.helper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;

import org.jadira.scanner.core.exception.ClasspathAccessException;

public final class JavassistAnnotationsHelper {

    private static final ClassPool CLASS_POOL = ClassPool.getDefault();
	
	private JavassistAnnotationsHelper() {
	}

	public static Annotation[] getAnnotationsForClass(ClassFile classFile) {

		AnnotationsAttribute visible = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
		AnnotationsAttribute invisible = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.invisibleTag);

		Set<Annotation> retVal = new HashSet<Annotation>();

		retVal.addAll(findAnnotationsForAnnotationsAttribute(visible));
		retVal.addAll(findAnnotationsForAnnotationsAttribute(invisible));

		return retVal.toArray(new Annotation[retVal.size()]);
	}
	
	public static Annotation[] getAnnotationsForMethod(MethodInfo methodInfo) {

		AnnotationsAttribute visible = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
		AnnotationsAttribute invisible = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.invisibleTag);

		Set<Annotation> retVal = new HashSet<Annotation>();

		retVal.addAll(findAnnotationsForAnnotationsAttribute(visible));
		retVal.addAll(findAnnotationsForAnnotationsAttribute(invisible));

		return retVal.toArray(new Annotation[retVal.size()]);
	}

	public static Annotation[] getAnnotationsForMethodParameter(MethodInfo methodInfo, int index) {

		ParameterAnnotationsAttribute visible = (ParameterAnnotationsAttribute) methodInfo.getAttribute(ParameterAnnotationsAttribute.visibleTag);
		ParameterAnnotationsAttribute invisible = (ParameterAnnotationsAttribute) methodInfo.getAttribute(ParameterAnnotationsAttribute.invisibleTag);

		Set<Annotation> retVal = new HashSet<Annotation>();

		retVal.addAll(findAnnotationsForAnnotationsArray(visible.getAnnotations()[index]));
		retVal.addAll(findAnnotationsForAnnotationsArray(invisible.getAnnotations()[index]));

		return retVal.toArray(new Annotation[retVal.size()]);
	}

	public static Annotation[] getAnnotationsForFieldInfo(FieldInfo fieldInfo) {

		AnnotationsAttribute visible = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);
		AnnotationsAttribute invisible = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.invisibleTag);

		Set<Annotation> retVal = new HashSet<Annotation>();

		retVal.addAll(findAnnotationsForAnnotationsAttribute(visible));
		retVal.addAll(findAnnotationsForAnnotationsAttribute(invisible));

		return retVal.toArray(new Annotation[retVal.size()]);
	}

	private static Set<Annotation> findAnnotationsForAnnotationsAttribute(AnnotationsAttribute attr) {

		if (attr != null) {
			javassist.bytecode.annotation.Annotation[] anns = attr.getAnnotations();
			return findAnnotationsForAnnotationsArray(anns);
		}
		return Collections.emptySet();
	}

	private static Set<Annotation> findAnnotationsForAnnotationsArray(javassist.bytecode.annotation.Annotation[] anns) {

		final Set<Annotation> retVal = new HashSet<Annotation>();
		for (javassist.bytecode.annotation.Annotation next : anns) {

			try {
				final Annotation toAdd = (Annotation) (next.toAnnotationType(JavassistAnnotationsHelper.class.getClassLoader(), CLASS_POOL));
				retVal.add(toAdd);
			} catch (ClassNotFoundException e) {
				throw new ClasspathAccessException("Problem finding class for annotation: " + e.getMessage(), e);
			}
		}
		return retVal;
	}
}
