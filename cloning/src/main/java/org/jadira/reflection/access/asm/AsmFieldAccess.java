/*
 *  Copyright 2013 Christopher Pheby
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
package org.jadira.reflection.access.asm;

import java.lang.reflect.Field;

import org.jadira.reflection.access.api.FieldAccess;

/**
 * FieldAccess implementation using an ASM based access strategy
 * @param <C> The Class containing the Field to be accessed
 */
public class AsmFieldAccess<C> implements FieldAccess<C> {

	private String fieldName;
	private AsmClassAccess<C> classAccess;
	private Class<C> declaringClass;
	private Class<?> fieldClass;
	private Field field;	

	@SuppressWarnings("unchecked")
	private AsmFieldAccess(AsmClassAccess<C> classAccess, Field f) {
		
		this.declaringClass = (Class<C>) f.getDeclaringClass();
		
		this.fieldClass = f.getType();
		
		this.classAccess = classAccess;		
		this.fieldName = f.getName();
		
		this.field = f;
	}
	
	@Override
	public Class<C> declaringClass() {
		return declaringClass;
	}

	@Override
	public Class<?> fieldClass() {
		return fieldClass;
	}

	@Override
	public Field field() {
		return field;
	}
	
	/**
	 * Get a new instance that can access the given Field
	 * @param classAccess The AsmClassAccess instance to be delegated to
	 * @param f Field to be accessed
	 * @return New AsmFieldAccess instance
	 */
	public static final <C> AsmFieldAccess<C> get(AsmClassAccess<C> classAccess, Field f) {
		return new AsmFieldAccess<C>(classAccess, f);
	}

	@Override
	public Object getValue(C parent) {
		return classAccess.getValue(parent, fieldName);
	}

	@Override
	public void putValue(C parent, Object newFieldValue) {
		classAccess.putValue(parent, fieldName, newFieldValue);
	}

	@Override
	public boolean getBooleanValue(C parent) {
		return classAccess.getBooleanValue(parent, fieldName);
	}

	@Override
	public byte getByteValue(C parent) {
		return classAccess.getByteValue(parent, fieldName);
	}

	@Override
	public char getCharValue(C parent) {
		return classAccess.getCharValue(parent, fieldName);
	}

	@Override
	public short getShortValue(C parent) {
		return classAccess.getShortValue(parent, fieldName);
	}

	@Override
	public int getIntValue(C parent) {
		return classAccess.getIntValue(parent, fieldName);
	}

	@Override
	public long getLongValue(C parent) {
		return classAccess.getLongValue(parent, fieldName);
	}

	@Override
	public float getFloatValue(C parent) {
		return classAccess.getFloatValue(parent, fieldName);
	}

	@Override
	public double getDoubleValue(C parent) {
		return classAccess.getDoubleValue(parent, fieldName);
	}

	@Override
	public void putBooleanValue(C parent, boolean newFieldValue) {
		classAccess.putBooleanValue(parent, fieldName, newFieldValue);
	}

	@Override
	public void putByteValue(C parent, byte newFieldValue) {
		classAccess.putByteValue(parent, fieldName, newFieldValue);
	}

	@Override
	public void putCharValue(C parent, char newFieldValue) {
		classAccess.putCharValue(parent, fieldName, newFieldValue);
	}

	@Override
	public void putShortValue(C parent, short newFieldValue) {
		classAccess.putShortValue(parent, fieldName, newFieldValue);
	}

	@Override
	public void putIntValue(C parent, int newFieldValue) {
		classAccess.putIntValue(parent, fieldName, newFieldValue);
	}

	@Override
	public void putLongValue(C parent, long newFieldValue) {
		classAccess.putLongValue(parent, fieldName, newFieldValue);
	}

	@Override
	public void putFloatValue(C parent, float newFieldValue) {
		classAccess.putFloatValue(parent, fieldName, newFieldValue);
	}

	@Override
	public void putDoubleValue(C parent, double newFieldValue) {
		classAccess.putDoubleValue(parent, fieldName, newFieldValue);
	}
}
