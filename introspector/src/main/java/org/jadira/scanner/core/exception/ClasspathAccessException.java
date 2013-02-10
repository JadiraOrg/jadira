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
package org.jadira.scanner.core.exception;

/**
 * Indicates a problem arising during introspection
 */
public class ClasspathAccessException extends RuntimeException {

    /**
     * serialVersionUID for this class
     */
    private static final long serialVersionUID = -2088658342781148714L;

    /**
     * Constructs an {@code ClasspathAccessException} with {@code null} as its error detail message.
     */
    public ClasspathAccessException() {
        super();
    }

    /**
     * Constructs an {@code ClasspathAccessException} with the specified detail message.
     * @param message The detail message (which is saved for later retrieval by the
     *            {@link #getMessage()} method)
     */
    public ClasspathAccessException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code ClasspathAccessException} with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i> automatically
     * incorporated into this exception's detail message.
     * @param message The detail message (which is saved for later retrieval by the
     *            {@link #getMessage()} method)
     * @param cause The cause (which is saved for later retrieval by the {@link #getCause()}
     *            method). (A null value is permitted, and indicates that the cause is nonexistent
     *            or unknown.)
     */
    public ClasspathAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code ClasspathAccessException} with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains the class and
     * detail message of {@code cause}). This constructor is useful for IntrospectionExceptions
     * that are little more than wrappers for other {@link Throwable} instances.
     * @param cause The cause (which is saved for later retrieval by the {@link #getCause()}
     *            method). (A null value is permitted, and indicates that the cause is nonexistent
     *            or unknown.)
     */
    public ClasspathAccessException(Throwable cause) {
        super(cause);
    }
}