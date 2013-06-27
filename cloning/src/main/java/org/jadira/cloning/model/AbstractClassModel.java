package org.jadira.cloning.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.jadira.cloning.annotation.Cloner;
import org.jadira.cloning.annotation.Immutable;
import org.jadira.cloning.annotation.NonCloneable;
import org.jadira.cloning.api.CloneImplementor;
import org.jadira.cloning.implementor.reflection.CopyConstructorImplementor;
import org.jadira.cloning.implementor.reflection.ReflectionMethodImplementor;
import org.jadira.cloning.mutability.MutabilityDetector;
import org.jadira.cloning.spi.ClassModel;
import org.jadira.cloning.spi.FieldModel;
import org.jadira.cloning.unsafe.FeatureDetection;

/**
 * Provides a base model resulting from introspection of a class
 */
public abstract class AbstractClassModel<F extends FieldModel> implements ClassModel<F>{

    private static final boolean MUTABILITY_DETECTOR_AVAILABLE = FeatureDetection.hasMutabilityDetector();

	private static final Class<Annotation> JSR305_IMMUTABLE_ANNOTATION;

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
    
    private final CloneImplementor cloneImplementor;
    
    protected AbstractClassModel(Class<?> modelClass) {
    
        this.modelClass = modelClass;
        
        if ((modelClass.getAnnotation(Immutable.class) != null)
        		|| (JSR305_IMMUTABLE_ANNOTATION != null && modelClass.getAnnotation(JSR305_IMMUTABLE_ANNOTATION) != null)
                || (MUTABILITY_DETECTOR_AVAILABLE && MutabilityDetector.getMutabilityDetector().isImmutable(modelClass))) {
            this.detectedAsImmutable = true;
        } else {
            this.detectedAsImmutable = false;
        }
        
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
    }
    
    public Class<?> getModelClass() {
        return modelClass;
    }

    
    /* (non-Javadoc)
     * @see org.jadira.cloning.portable.ClassModel#isDetectedAsImmutable()
     */
    @Override
    public boolean isDetectedAsImmutable() {
        return detectedAsImmutable;
    }
    
    /* (non-Javadoc)
     * @see org.jadira.cloning.portable.ClassModel#isNonCloneable()
     */
    @Override
    public boolean isNonCloneable() {
        return nonCloneable;
    }
    

    @Override
    public CloneImplementor getCloneImplementor() {
        return cloneImplementor;
    }
}
