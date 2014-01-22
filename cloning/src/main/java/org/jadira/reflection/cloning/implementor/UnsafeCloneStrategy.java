/*
 *  Copyright 2013 Chris Pheby
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
package org.jadira.reflection.cloning.implementor;

import java.util.IdentityHashMap;

import org.jadira.reflection.access.model.ClassModel;
import org.jadira.reflection.access.model.FieldModel;
import org.jadira.reflection.access.unsafe.UnsafeClassAccess;
import org.jadira.reflection.access.unsafe.UnsafeOperations;
import org.jadira.reflection.cloning.api.CloneDriver;
import org.jadira.reflection.cloning.api.CloneStrategy;
import org.objenesis.ObjenesisException;

/**
 * A CloneStrategy that uses sun.misc.Unsafe
 */
public class UnsafeCloneStrategy extends AbstractCloneStrategy implements CloneStrategy {

    private static final UnsafeOperations UNSAFE_OPERATIONS = UnsafeOperations.getUnsafeOperations();
    
    @Override
    public <T> T newInstance(Class<T> c) {
        try {
            return UNSAFE_OPERATIONS.allocateInstance(c);
        } catch (IllegalStateException e) {
            throw new ObjenesisException(e.getCause());
        }
    }

    private static UnsafeCloneStrategy instance = new UnsafeCloneStrategy();

    /**
     * Returns a shared instance of UnsafeCloneStrategy
     * @return
     */
    public static UnsafeCloneStrategy getInstance() {
        return instance;
    }
    
    @Override
    protected <W> ClassModel<W> getClassModel(Class<W> clazz) {
        return ClassModel.get(UnsafeClassAccess.get(clazz));
    }
    
    @Override
    protected <T> void handleTransientField(T copy, FieldModel<T> f) {
    	
    	Class<?> type = f.getFieldClass();
        if (type.isPrimitive()) {
    		if (java.lang.Boolean.TYPE == type) {
    			f.getFieldAccess().putBooleanValue(copy, false);
    		} else if (java.lang.Byte.TYPE == type) {
    			f.getFieldAccess().putByteValue(copy, (byte) 0);
    		} else if (java.lang.Character.TYPE == type) {
    			f.getFieldAccess().putCharValue(copy, '\u0000');
    		} else if (java.lang.Short.TYPE == type) {
    			f.getFieldAccess().putShortValue(copy, (short) 0);
    		} else if (java.lang.Integer.TYPE == type) {
    			f.getFieldAccess().putIntValue(copy, 0);
    		} else if (java.lang.Long.TYPE == type) {
    			f.getFieldAccess().putLongValue(copy, 0L);
    		} else if (java.lang.Float.TYPE == type) {
    			f.getFieldAccess().putFloatValue(copy, 0.0f);
    		} else if (java.lang.Double.TYPE == type) {
    			f.getFieldAccess().putDoubleValue(copy, 0.0d);
    		}
        } else {
        	f.getFieldAccess().putValue(copy, null);
        }
    }

    @Override
    protected <T> void handleClonePrimitiveField(T obj, T copy, CloneDriver driver, FieldModel<T> f,
            IdentityHashMap<Object, Object> referencesToReuse) {
    	
    	Class<?> type = f.getFieldClass();
        if (type.isPrimitive()) {
    		if (java.lang.Boolean.TYPE == type) {
    			f.getFieldAccess().putBooleanValue(copy, f.getFieldAccess().getBooleanValue(obj));
    		} else if (java.lang.Byte.TYPE == type) {
    			f.getFieldAccess().putByteValue(copy, f.getFieldAccess().getByteValue(obj));
    		} else if (java.lang.Character.TYPE == type) {
    			f.getFieldAccess().putCharValue(copy, f.getFieldAccess().getCharValue(obj));
    		} else if (java.lang.Short.TYPE == type) {
    			f.getFieldAccess().putShortValue(copy, f.getFieldAccess().getShortValue(obj));
    		} else if (java.lang.Integer.TYPE == type) {
    			f.getFieldAccess().putIntValue(copy, f.getFieldAccess().getIntValue(obj));
    		} else if (java.lang.Long.TYPE == type) {
    			f.getFieldAccess().putLongValue(copy, f.getFieldAccess().getLongValue(obj));
    		} else if (java.lang.Float.TYPE == type) {
    			f.getFieldAccess().putFloatValue(copy, f.getFieldAccess().getFloatValue(obj));
    		} else if (java.lang.Double.TYPE == type) {
    			f.getFieldAccess().putDoubleValue(copy, f.getFieldAccess().getDoubleValue(obj));
    		}
        } else {
        	f.getFieldAccess().putValue(copy, f.getFieldAccess().getValue(obj));
        }
    }

    @Override
    protected <T> Object getFieldValue(T obj, FieldModel<T> f) {
        return f.getFieldAccess().getValue(obj);
    }

    @Override
    protected <T> void putFieldValue(T obj, FieldModel<T> f, Object value) {
    	f.getFieldAccess().putValue(obj, value);
    }
}
