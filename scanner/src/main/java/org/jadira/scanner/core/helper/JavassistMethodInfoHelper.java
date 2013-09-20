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

import javassist.Modifier;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public final class JavassistMethodInfoHelper {

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
}
