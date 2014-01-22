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
package org.jadira.reflection.access.invokedynamic;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

import org.dynalang.dynalink.DefaultBootstrapper;
import org.jadira.reflection.access.api.FieldAccess;

/**
 * FieldAccess implementation using an InvokeDynamic based strategy (using ASM and Dynalang)
 * @param <C> The Class containing the Field to be accessed
 */
public class InvokeDynamicFieldAccess<C> implements FieldAccess<C> {

    private String fieldName;
	
	private Class<C> declaringClass;
	private Class<?> fieldClass;
	private Field field;	

	CallSite getCallSite;
	CallSite setCallSite;
	
	MethodHandle setMh;
	MethodHandle getMh;
	
	@SuppressWarnings("unchecked")
	private InvokeDynamicFieldAccess(InvokeDynamicClassAccess<C> classAccess, Field f) {
		
		this.declaringClass = (Class<C>) f.getDeclaringClass();
		
		this.fieldClass = f.getType();
		
		this.fieldName = f.getName();
		
		this.field = f;
		
		setCallSite = DefaultBootstrapper.publicBootstrap(null, "dyn:setProp:" + fieldName, MethodType.methodType(void.class, Object.class, fieldClass));
		getCallSite = DefaultBootstrapper.publicBootstrap(null, "dyn:getProp:" + fieldName, MethodType.methodType(fieldClass, Object.class));
		
		setMh = setCallSite.dynamicInvoker();
	    getMh = getCallSite.dynamicInvoker();
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
	 * @param classAccess The InvokeDynamicClassAccess instance to be delegated to
	 * @param f Field to be accessed
	 * @return New InvokeDynamicFieldAccess instance
	 */
	public static final <C> InvokeDynamicFieldAccess<C> get(InvokeDynamicClassAccess<C> classAccess, Field f) {
		return new InvokeDynamicFieldAccess<C>(classAccess, f);
	}

	@Override
	public Object getValue(C parent) {
        try {
            return getMh.invokeExact(parent);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public void putValue(C parent, Object newFieldValue) {
	    try {
            setMh.invokeExact(parent, newFieldValue);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public boolean getBooleanValue(C parent) {
        try {
            return (boolean) getMh.invokeExact(parent);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public byte getByteValue(C parent) {
        try {
            return (byte) getMh.invokeExact(parent);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public char getCharValue(C parent) {
        try {
            return (char) getMh.invokeExact(parent);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public short getShortValue(C parent) {
        try {
            return (short) getMh.invokeExact(parent);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public int getIntValue(C parent) {
        try {
            return (int) getMh.invokeExact(parent);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public long getLongValue(C parent) {
        try {
            return (long) getMh.invokeExact(parent);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public float getFloatValue(C parent) {
        try {
            return (float) getMh.invokeExact(parent);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public double getDoubleValue(C parent) {
        try {
            return (double) getMh.invokeExact(parent);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public void putBooleanValue(C parent, boolean newFieldValue) {
	    try {
            setMh.invokeExact(parent, newFieldValue);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public void putByteValue(C parent, byte newFieldValue) {
	    try {
            setMh.invokeExact(parent, newFieldValue);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public void putCharValue(C parent, char newFieldValue) {
	    try {
            setMh.invokeExact(parent, newFieldValue);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public void putShortValue(C parent, short newFieldValue) {
	    try {
            setMh.invokeExact(parent, newFieldValue);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public void putIntValue(C parent, int newFieldValue) {
	    try {
            setMh.invokeExact(parent, newFieldValue);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public void putLongValue(C parent, long newFieldValue) {
	    try {
            setMh.invokeExact(parent, newFieldValue);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public void putFloatValue(C parent, float newFieldValue) {
	    try {
            setMh.invokeExact(parent, newFieldValue);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public void putDoubleValue(C parent, double newFieldValue) {
	    try {
            setMh.invokeExact(parent, newFieldValue);
        } catch (Throwable e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}
}
