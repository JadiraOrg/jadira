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

import java.util.Arrays;

import javassist.Modifier;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import org.jadira.scanner.core.exception.ClasspathAccessException;

public class JavassistMethodInfoHelper {

	private JavassistMethodInfoHelper() {
	}

	public static String[] getMethodParamTypeNames(MethodInfo methodInfo) {
		String desc = methodInfo.getDescriptor();

		String paramsString = desc.substring(desc.indexOf('(') + 1, desc.lastIndexOf(')'));

		if (paramsString.length() == 0) {
			return new String[0];
		}

		String[] classNames = paramsString.split(";");
		return classNames;
	}

	public static String[] getMethodParamNames(MethodInfo methodInfo) {

		String[] retVal = new String[getMethodParamTypeNames(methodInfo).length];

		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

		LocalVariableAttribute localVarAttr = null;
		if (codeAttribute != null) {
			AttributeInfo attributeInfo = codeAttribute.getAttribute("LocalVariableTable");
			localVarAttr = (LocalVariableAttribute) attributeInfo;
		}


		int j = 0;

		if (Modifier.isSynchronized(methodInfo.getAccessFlags())) {
			j = j + 1;
		}
		if (!Modifier.isStatic(methodInfo.getAccessFlags())) {
			j = j + 1;
		}
		
		for (int i = 0; i < retVal.length; i++) {

			if (localVarAttr == null) {
				retVal[i] = "" + i;
			} else {
				retVal[i] = localVarAttr.variableName(i + j);
			}
		}
		return retVal;
	}

	public static Class<?>[] getMethodParamClasses(MethodInfo methodInfo) throws ClasspathAccessException {

		String[] classNames = getMethodParamTypeNames(methodInfo);
		Class<?>[] retArray = new Class<?>[classNames.length];

		for (int i = 0; i < classNames.length; i++) {
			if (!"".equals(classNames[i])) {
				try {
					retArray[i] = decodeFieldType(classNames[i]); // Class.forName(classNames[i]);
				} catch (ClassNotFoundException e) {
					throw new ClasspathAccessException("Class could not be found: " + e.getMessage(), e);
				}
			}
		}
		return retArray;
	}

	public static Class<?> decodeFieldType(String componentType) throws ClassNotFoundException {

		char type = componentType.charAt(0);
		String fieldContent = componentType.substring(1);

		switch (type) {
		case 'L': // L<classname>; reference an instance of class <classname>
			return Class.forName(fieldContent.replace('/', '.'));
		case 'B': // B byte signed byte
			return Byte.class;
		case 'C': // C char Unicode character
			return Character.class;
		case 'D': // D double double-precision floating-point value
			return Double.class;
		case 'F': // F float single-precision floating-point value
			return Float.class;
		case 'I': // I int integer
			return Integer.class;
		case 'J': // J long long integer
			return Long.class;
		case 'S': // S short signed short
			return Short.class;
		case 'Z': // Z boolean true or false
			return Boolean.class;
		case '[': // [ reference one array dimension
			return Arrays.class; // Class.forName(componentType.replace('/', '.') + ";");
		}
		return null;
	}
}
