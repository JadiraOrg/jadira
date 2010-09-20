/*
 *  Copyright 2010 Christopher Pheby
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
package org.jadira.usertype.dateandtime.shared.reflectionutils;

import java.lang.reflect.InvocationTargetException;

public class ReflectionException extends RuntimeException {

	private static final long serialVersionUID = 338232184843559586L;

	public ReflectionException(NoSuchMethodException e) {
        super(e.getMessage(), e);
    }

	public ReflectionException(String string, SecurityException e) {
		super(e.getMessage(), e);
	}

	public ReflectionException(String string, NoSuchFieldException e) {
		super(e.getMessage(), e);
	}

	public ReflectionException(String string, IllegalArgumentException e) {
		super(e.getMessage(), e);
	}

	public ReflectionException(String string, IllegalAccessException e) {
		super(e.getMessage(), e);
	}

	public ReflectionException(String string, NoSuchMethodException e) {
		super(e.getMessage(), e);
	}

	public ReflectionException(String string, InvocationTargetException e) {
		super(e.getMessage(), e);
	}
}
