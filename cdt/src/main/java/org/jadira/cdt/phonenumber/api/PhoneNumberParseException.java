/*
 *  Copyright 2012 Christopher Pheby
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
package org.jadira.cdt.phonenumber.api;

import org.jadira.cdt.exception.WrappedRuntimeException;

import com.google.i18n.phonenumbers.NumberParseException;

/**
 * Represents an error caused during the parsing of a phone number.
 */
public class PhoneNumberParseException extends WrappedRuntimeException {

	private static final long serialVersionUID = -24876358310884916L;

	/**
	 * Creates a new instance for the given {@link NumberParseException}
	 * @param message The exception's message 
	 * @param e The causing exception
	 */
	public PhoneNumberParseException(String message, NumberParseException e) {
		super(message, e);
	}

	/**
	 * Creates a new instance with the given message
	 * @param message The exception's message 
	 */
	public PhoneNumberParseException(String message) {
		super(message);
	}
}
