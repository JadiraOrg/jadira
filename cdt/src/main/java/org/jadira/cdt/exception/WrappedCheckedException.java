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
package org.jadira.cdt.exception;

/**
 * Class for constructing checked Exceptions with a given root cause.
 */
public abstract class WrappedCheckedException extends Exception {

	private static final long serialVersionUID = 615855181745250021L;

	/**
	 * Construct a WrappedCheckedException with the specified 
	 * message.
	 * @param message the specific message
	 */
	public WrappedCheckedException(String message) {
		super(message);
	}

	/**
	 * Construct a WrappedCheckedException with the specified 
	 * message and wrapped exception.
	 * @param message the specific message
	 * @param cause the wrapped exception
	 */
	public WrappedCheckedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Return the detail message, including the message from the Wrapped
	 * exception if there is one.
	 */
	@Override
	public String getMessage() {
		return constructMessage(super.getMessage(), getCause());
	}

	/**
	 * Constructs an exception String with the given message and incorporating the
	 * causing exception
	 * @param message The specific message
	 * @param cause The causing exception
	 * @return The exception String
	 */
	protected String constructMessage(String message, Throwable cause) {
		
		if (cause != null) {
		
			StringBuilder strBuilder = new StringBuilder();
			
			if (message != null) {
				strBuilder.append(message).append(": ");
			}
			
			strBuilder.append("Wrapped exception is {").append(cause);
			strBuilder.append("}");
			
			return strBuilder.toString();
			
		} else {
			return message;
		}
	}

	/**
	 * Retrieves the ultimate root cause for this exception, or null
	 */
	public Throwable getRootCause() {
		
		Throwable rootCause = null;
		Throwable nextCause = getCause();
		
		while (nextCause != null && !nextCause.equals(rootCause)) {
			rootCause = nextCause;
			nextCause = nextCause.getCause();
		}
		
		return rootCause;
	}

	/**
	 * Returns the next parent exception of the given type, or null
	 * @param exceptionType the exception type to match
	 * @return The matched exception of the target type, or null
	 */
	public <E extends Exception> E findWrapped(Class<E> exceptionType) {
		
		if (exceptionType == null) {
			return null;
		}
		
		Throwable cause = getCause();
		
		while (true) {
			
			if (cause == null) {
				return null;
			}
			
			if (exceptionType.isInstance(cause)) {
				
				@SuppressWarnings("unchecked") E matchedCause = (E) cause;
				return matchedCause;
			}

			if (cause.getCause() == cause) {
				return null;
			}
			
			if (cause instanceof WrappedRuntimeException) {
				return ((WrappedRuntimeException) cause).findWrapped(exceptionType);
			}
			if (cause instanceof WrappedCheckedException) {
				return ((WrappedCheckedException) cause).findWrapped(exceptionType);
			}			
			
			cause = cause.getCause();
		}
	}
}
