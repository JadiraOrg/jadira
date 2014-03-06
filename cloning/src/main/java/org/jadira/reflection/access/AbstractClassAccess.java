package org.jadira.reflection.access;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.access.api.FieldAccess;
import org.jadira.reflection.access.api.MethodAccess;
import org.jadira.reflection.access.model.ClassModel;
import org.jadira.reflection.core.misc.ClassUtils;

public abstract class AbstractClassAccess<C> implements ClassAccess<C> {

	/**
	 * The class to be accessed
	 */
	private Class<C> clazz;

	/**
	 * An ordered array giving the names of the fields in the class to accessed
	 */
	protected final String[] fieldNames;

	/**
	 * An ordered array giving the Fields in the class to accessed
	 */
	private final Field[] fields;

	private final FieldAccess<C>[] fieldAccess;

	/**
	 * An ordered array giving the names of the methods in the class to accessed
	 */
    private final String[] methodNames;
	
	/**
	 * An ordered array giving the Methods in the class to accessed
	 */
    private final Method[] methods;
    
    private final MethodAccess<C>[] methodAccess;

	private final ClassModel<C> classModel;

	private final ClassAccess<? super C> superClassAccess;

	private final boolean providesHashCode;
	private final boolean providesEquals;
	
	/**
	 * Constructor, intended for use by generated subclasses
	 * @param clazz The Class to be accessed
	 */
	@SuppressWarnings("unchecked")
	protected AbstractClassAccess(Class<C> clazz) {

		this.clazz = clazz;

		Field[] myFields = ClassUtils.collectDeclaredInstanceFields(clazz);

		String[] unsortedFieldNames = new String[myFields.length];
		for (int i = 0; i < unsortedFieldNames.length; i++) {
			unsortedFieldNames[i] = myFields[i].getName();
		}
		fieldNames = Arrays.copyOf(unsortedFieldNames, unsortedFieldNames.length);
		Arrays.sort(fieldNames);

		final FieldAccess<C>[] myFieldAccess = (FieldAccess<C>[]) new FieldAccess[myFields.length];
		fields = new Field[myFields.length];

		for (int i = 0; i < fields.length; i++) {

			String fieldName = unsortedFieldNames[i];
			for (int tIdx = 0; tIdx < unsortedFieldNames.length; tIdx++) {
				if (fieldName.equals(fieldNames[tIdx])) {
					myFieldAccess[tIdx] = constructFieldAccess(myFields[i]);
					fields[tIdx] = myFields[i];
					break;
				}
			}

		}
		fieldAccess = myFieldAccess;
		
        Method[] myMethods = ClassUtils.collectDeclaredMethods(clazz);
        
        String[] unsortedMethodNames = new String[myMethods.length];
        for (int i=0; i < unsortedMethodNames.length; i++) {
            unsortedMethodNames[i] = myMethods[i].getName();
        }
        methodNames = Arrays.copyOf(unsortedMethodNames, unsortedMethodNames.length);
        Arrays.sort(methodNames);
        
        final MethodAccess<C>[] myMethodAccess = (MethodAccess<C>[])new MethodAccess[myMethods.length];
        methods = new Method[myMethods.length];
        
        for (int i=0; i < methods.length; i++) {
   
            String methodName = unsortedMethodNames[i];
            for (int tIdx = 0; tIdx < unsortedMethodNames.length; tIdx++) {
                if (methodName.equals(methodNames[tIdx])) {
                    myMethodAccess[tIdx] = constructMethodAccess(myMethods[i]);
                    methods[tIdx] = myMethods[i];
                    break;
                }
            }
        }
        methodAccess = myMethodAccess;
		
		final Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			this.superClassAccess = constructClassAccess(clazz.getSuperclass());
		} else {
			this.superClassAccess = null;
		}
        
		this.classModel = ClassModel.get(this);
			
		Class<?> hashcodeClass;
		try {
			hashcodeClass = clazz.getMethod("hashCode").getDeclaringClass();
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("hashCode() method could not be found: " + e.getMessage(), e);
		} catch (SecurityException e) {
			throw new IllegalStateException("hashCode() method could not be accessed: " + e.getMessage(), e);
		}
		
		providesHashCode = hashcodeClass.equals(clazz);
		
		Class<?> equalsClass;
		try {
			equalsClass = clazz.getMethod("equals", Object.class).getDeclaringClass();
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("equals() method could not be found: " + e.getMessage(), e);
		} catch (SecurityException e) {
			throw new IllegalStateException("equals() method could not be accessed: " + e.getMessage(), e);
		}
		
		providesEquals = equalsClass.equals(clazz);
	}
	
	@Override
	public Class<C> getType() {
		return clazz;
	}

	@Override
	public FieldAccess<C>[] getDeclaredFieldAccessors() {
		return fieldAccess;
	}

	@Override
	public FieldAccess<C> getDeclaredFieldAccess(Field f) {
		int idx = Arrays.binarySearch(fieldNames, f.getName());
		return fieldAccess[idx];
	}
	

    @Override
    public MethodAccess<C>[] getDeclaredMethodAccessors() {
        return methodAccess;
    }
    
	@Override
	public MethodAccess<C> getDeclaredMethodAccess(Method m) {
		int idx = Arrays.binarySearch(methodNames, m.getName());
		if (methodAccess[idx].method().equals(m)) {
			return methodAccess[idx];
		}
		int backTrack = idx;
		while (true) {
			if (methodAccess[backTrack].method().equals(m)) {
				return methodAccess[idx];
			}
			if (!(methodAccess[backTrack].method().getName()
					.equals(m.getName()))) {
				break;
			}
			backTrack = backTrack - 1;
		}
		while (true) {
			idx = idx + 1;
			if (methodAccess[idx].method().equals(m)) {
				return methodAccess[idx];
			}
			if (!(methodAccess[idx].method().getName().equals(m.getName()))) {
				break;
			}
		}
		return null;
	}
	
	@Override
	public ClassModel<C> getClassModel() {
		return classModel;
	}

	protected String[] getDeclaredFieldNames() {
		return fieldNames;
	}
	
	protected Field[] getDeclaredFields() {
		return fields;
	}

	protected String[] getDeclaredMethodNames() {
		return methodNames;
	}
	
	protected Method[] getDeclaredMethods() {
		return methods;
	}
	
	@Override
	public ClassAccess<? super C> getSuperClassAccess() {
		return superClassAccess;
	}
	

	@Override
	public boolean providesHashCode() {
		return providesHashCode;
	}
	
	@Override
	public boolean providesEquals() {
		return providesEquals;
	}
	
	protected abstract FieldAccess<C> constructFieldAccess(Field field);
	
	protected abstract MethodAccess<C> constructMethodAccess(Method method);
	
	protected abstract <X> ClassAccess<X> constructClassAccess(Class<X> clazz);
}
