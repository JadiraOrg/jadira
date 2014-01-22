/*
 *  Copyright 2013 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LIObjectENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR ObjectONDITIONS OObject ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.reflection.access.api;

import java.lang.reflect.Field;

/**
 * Defines a mechanism for accessing a specific field within a specific class
 * @param <C> The class containing the field to be accessed
 */
public interface FieldAccess<C> {

	/**
	 * Get the Class containing the field being accessed
	 * @return The class
	 */
	public Class<C> declaringClass();
	
	/**
	 * Get the type for the field being accessed
	 * @return The class of the field
	 */
	public Class<?> fieldClass();
	
	/**
	 * Get the Field being accessed
	 * @return The field
	 */
	public Field field();
	
	/**
	 * Retrieve the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @return The field value as an object
	 */
	Object getValue(C parent);
	
	/**
	 * Update the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @param newFieldValue The new value
	 */
	void putValue(C parent, Object newFieldValue);

	/**
	 * Retrieve the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @return The field value as a boolean
	 */
	boolean getBooleanValue(C parent);

	/**
	 * Update the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @param newFieldValue The new value as a boolean
	 */
	void putBooleanValue(C parent, boolean newFieldValue);

	/**
	 * Retrieve the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @return The field value as a byte
	 */
	byte getByteValue(C parent);

	/**
	 * Update the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @param newFieldValue The new value as a byte
	 */
	void putByteValue(C parent, byte newFieldValue);
	
	/**
	 * Retrieve the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @return The field value as a char
	 */
	char getCharValue(C parent);
	
	/**
	 * Update the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @param newFieldValue The new value as a char
	 */
	void putCharValue(C parent, char newFieldValue);
	
	/**
	 * Retrieve the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @return The field value as a short
	 */
	short getShortValue(C parent);
	
	/**
	 * Update the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @param newFieldValue The new value as a short
	 */
	void putShortValue(C parent, short newFieldValue);
	
	/**
	 * Retrieve the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @return The field value as an int
	 */
	int getIntValue(C parent);
	
	/**
	 * Update the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @param newFieldValue The new value as an int
	 */
	void putIntValue(C parent, int newFieldValue);
	
	/**
	 * Retrieve the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @return The field value as a long
	 */
	long getLongValue(C parent);
	
	/**
	 * Update the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @param newFieldValue The new value as a long
	 */
	void putLongValue(C parent, long newFieldValue);

	/**
	 * Retrieve the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @return The field value as a float
	 */
	float getFloatValue(C parent);
	
	/**
	 * Update the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @param newFieldValue The new value as a float
	 */
	void putFloatValue(C parent, float newFieldValue);
	
	/**
	 * Retrieve the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @return The field value as a double
	 */
	double getDoubleValue(C parent);
	
	/**
	 * Update the value of the field for the given instance
	 * @param parent The instance to access the field for
	 * @param newFieldValue The new value as a double
	 */
	void putDoubleValue(C parent, double newFieldValue);
}
