package org.jadira.reflection.access.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.cloning.annotation.Cloner;
import org.jadira.reflection.cloning.annotation.Flat;
import org.jadira.reflection.cloning.annotation.Immutable;
import org.jadira.reflection.cloning.annotation.NonCloneable;
import org.jadira.reflection.cloning.api.CloneImplementor;
import org.jadira.reflection.cloning.implementor.reflection.CopyConstructorImplementor;
import org.jadira.reflection.cloning.implementor.reflection.ReflectionMethodImplementor;
import org.jadira.reflection.cloning.mutability.MutabilityDetector;
import org.jadira.reflection.core.misc.ClassUtils;
import org.jadira.reflection.core.platform.FeatureDetection;
import org.mutabilitydetector.checkers.MutabilityAnalysisException;

/**
 * Provides a base model resulting from introspection of a class
 */
public class ClassModel<C> {

	private static final boolean MUTABILITY_DETECTOR_AVAILABLE = FeatureDetection.hasMutabilityDetector();

	private static final Class<Annotation> JSR305_IMMUTABLE_ANNOTATION;

    private static final ConcurrentHashMap<String, ClassModel<?>> classModels = new ConcurrentHashMap<String, ClassModel<?>>(16);
    
	private static final Object MONITOR = new Object();
	
	static {	
		Class<Annotation> immutableAnnotation;
		try {
			@SuppressWarnings("unchecked")
			final Class<Annotation> myImmutableAnnotation = (Class<Annotation>) Class.forName("javax.annotation.concurrent.Immutable");
			immutableAnnotation = myImmutableAnnotation;
		} catch (ClassNotFoundException e) {
			immutableAnnotation = null;
		}
		JSR305_IMMUTABLE_ANNOTATION = immutableAnnotation;
	}

	private final Class<?> modelClass;
	private final boolean detectedAsImmutable;
	private final boolean nonCloneable;
	private final boolean flat;

	private final CloneImplementor cloneImplementor;

	private final ClassAccess<C> classAccess;

	private FieldModel<C>[] modelFields;
	
	private ClassModel<? super C> superClassModel;

    /**
     * Returns a class model for the given ClassAccess instance. If a ClassModel 
     * already exists, it will be reused.
     * @param classAccess The ClassAccess
     * @param <C> The type of class
     * @return The Field Model
     */
	@SuppressWarnings("unchecked")
	public static final <C> ClassModel<C> get(ClassAccess<C> classAccess) {
        
		Class<?> clazz = classAccess.getType();
		
		String classModelKey = (classAccess.getClass().getName() + ":" + clazz.getName());
		
		ClassModel<C> classModel = (ClassModel<C>)classModels.get(classModelKey);
    	if (classModel != null) {       	
        	return classModel;
        }
    	
    	synchronized(MONITOR) {
    		classModel = (ClassModel<C>)classModels.get(classModelKey);
        	if (classModel != null) {       	
            	return classModel;
            } else {
            	classModel = new ClassModel<C>(classAccess);
            	classModels.put(classModelKey, classModel);
            	
            	return classModel;
            }
    	}
    }
	
	private ClassModel(ClassAccess<C> classAccess) {

		this.classAccess = classAccess;
		this.modelClass = classAccess.getType();
		
		boolean myDetectedAsImmutable = false;
		try {
			if (((modelClass == Object.class) || modelClass.getAnnotation(Immutable.class) != null) || (JSR305_IMMUTABLE_ANNOTATION != null && modelClass.getAnnotation(JSR305_IMMUTABLE_ANNOTATION) != null)
					|| (MUTABILITY_DETECTOR_AVAILABLE && MutabilityDetector.getMutabilityDetector().isImmutable(modelClass))) {
				myDetectedAsImmutable = true; 
			}
		} catch (MutabilityAnalysisException e) {
		}
		this.detectedAsImmutable = myDetectedAsImmutable;

		Method clonerMethod = null;
		Constructor<?> clonerConstructor = null;
		for (Method m : modelClass.getDeclaredMethods()) {
			if (m.getAnnotation(Cloner.class) != null) {
				if (clonerMethod != null) {
					throw new IllegalStateException("Only one cloner method may be declared on a class");
				} else {
					clonerMethod = m;
				}
			}
		}
		Constructor<?> c = null;
		try {
			c = modelClass.getConstructor(modelClass);
		} catch (NoSuchMethodException e) {
			// Ignore
		} catch (SecurityException e) {
			// Ignore
		}
		if (c != null && (c.getAnnotation(Cloner.class) != null)) {
			if (clonerMethod != null) {
				throw new IllegalStateException("Only one cloner method may be declared on a class");
			} else {
				clonerConstructor = c;
			}
		}

		if (clonerMethod != null) {
			this.cloneImplementor = new ReflectionMethodImplementor(clonerMethod);
		} else if (clonerConstructor != null) {
			this.cloneImplementor = new CopyConstructorImplementor(clonerConstructor);
		} else {
			cloneImplementor = null;
		}

		this.nonCloneable = modelClass.getAnnotation(NonCloneable.class) != null;

		this.flat = modelClass.getAnnotation(Flat.class) != null;
		
		Field[] fields = ClassUtils.collectDeclaredInstanceFields(modelClass);
		
		@SuppressWarnings("unchecked")
		final FieldModel<C>[] myModelFields = (FieldModel<C>[])new FieldModel[fields.length];
		for (int i=0; i < fields.length; i++) {
			myModelFields[i] = FieldModel.get(fields[i], classAccess.getDeclaredFieldAccess(fields[i]));
		}
		modelFields = myModelFields;

		ClassAccess<? super C> superClassAccess = classAccess.getSuperClassAccess();
		if (superClassAccess != null) {
			superClassModel = ClassModel.get(superClassAccess);
		}
	}
	
	/**
	 * Access the Class associated with the ClassModel
	 * @return The associated Class
	 */
	public Class<?> getModelClass() {
		return modelClass;
	}

    /**
     * Access the ClassAccess associated with the ClassModel
     * @return The associated ClassAccess.
     */
	public ClassAccess<C> getClassAccess() {
		return classAccess;
	}
	
	/**
	 * Access the model for the super class
	 * @return The associated ClassModel
	 */
	public ClassModel<? super C> getSuperClassModel() {
		return superClassModel;
	}

    /**
     * Indicates whether the class has been determined to be immutable
     * @return True if detected as immutable
     */
	public boolean isDetectedAsImmutable() {
		return detectedAsImmutable;
	}

    /**
     * Indicates whether the class should be treated as Non-Cloneable. Such a class should not
     * be cloned - i.e. the same instance should be returned (as with immutable objects)
     * @return True if detected as non-cloneable
     */
	public boolean isNonCloneable() {
		return nonCloneable;
	}

    /**
     * Indicates whether the class should be treated as Flat. When a Flat class is encountered,
     * references are no longer tracked as it is assumed that any reference only appears once.
     * This allows a significant increase in performance
     * @return True if detected as flat
     */
	public boolean isFlat() {
		return flat;
	}

    /**
     * If there is a method or constructor configured as a @Cloner instance, the CloneImplementor 
     * that can invoke this method will be returned
     * @return The configured CloneImplementor or null
     */
	public CloneImplementor getCloneImplementor() {
		return cloneImplementor;
	}

	/**
	 * Return an array of FieldModel for the class - one entry per Field
	 * @return The FieldModels for the class
	 */
	public FieldModel<C>[] getModelFields() {
		return modelFields;
	}
}
