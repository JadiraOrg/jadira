package org.jadira.cloning.implementor;

import java.util.IdentityHashMap;

import org.jadira.cloning.api.CloneDriver;
import org.jadira.cloning.api.CloneStrategy;
import org.jadira.cloning.model.UnsafeClassModel;
import org.jadira.cloning.model.UnsafeFieldModel;
import org.jadira.cloning.portable.ClassUtils;
import org.jadira.cloning.portable.FieldType;
import org.jadira.cloning.unsafe.UnsafeOperations;
import org.objenesis.ObjenesisException;

public class UnsafeCloneStrategy extends AbstractCloneStrategy<UnsafeClassModel, UnsafeFieldModel> implements CloneStrategy {

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

    public static UnsafeCloneStrategy getInstance() {
        return instance;
    }

    @Override
    protected <T> void handleCloneField(T obj, T copy, CloneDriver driver, UnsafeFieldModel f, IdentityHashMap<Object, Object> referencesToReuse) {
        
        if (f.getFieldType() == FieldType.PRIMITIVE) {
            UNSAFE_OPERATIONS.copyPrimitiveAtOffset(obj, copy, f.getFieldClass(), f.getOffset());
        } else {
            
            Object origFieldValue = UNSAFE_OPERATIONS.getObject(obj, f.getOffset());
            
            if (origFieldValue == null) {
                UNSAFE_OPERATIONS.putNullObject(copy, f.getOffset());
            } else {
                final Object copyFieldValue;
                if ((ClassUtils.isJdkImmutable(f.getFieldClass()))
                		|| (!driver.isCloneSyntheticFields() && f.isSynthetic())) {
                    copyFieldValue = origFieldValue;
                } else {
                    copyFieldValue = clone(origFieldValue, driver, referencesToReuse);
                }

                UNSAFE_OPERATIONS.putObject(copy, f.getOffset(), copyFieldValue);
            }
        }
    }
    
    @Override
    protected UnsafeClassModel getClassModel(Class<?> clazz) {
        return UnsafeClassModel.get(clazz);
    }
    
    @Override
    protected <T> void handleTransientField(T copy, UnsafeFieldModel f) {
        if (f.getFieldType() == FieldType.PRIMITIVE) {
            UNSAFE_OPERATIONS.putPrimitiveDefaultAtOffset(copy, f.getFieldClass(), f.getOffset());
        } else {
            UNSAFE_OPERATIONS.putNullObject(copy, f.getOffset());
        }
    }
}
