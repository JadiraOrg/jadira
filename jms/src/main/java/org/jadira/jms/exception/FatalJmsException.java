/*
 *  Copyright 2010, 2013, 2014 Chris Pheby
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
package org.jadira.jms.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Indicates a problem occurred during JMS processing that cannot be recovered from. For example, a poison message
 */
public class FatalJmsException extends NestedRuntimeException {

    private static final long serialVersionUID = -395763375849291308L;

    /**
     * Create a new instance for the given message
     * @param message The message
     */
    public FatalJmsException(final String message) {
        super(message);
    }

    /**
     * Create a new instance for the given message and cause
     * @param message The message
     * @param cause The causing Throwable
     */
    public FatalJmsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
