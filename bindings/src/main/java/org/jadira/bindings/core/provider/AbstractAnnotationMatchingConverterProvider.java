/*
 *  Copyright 2010, 2011 Chris Pheby
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
package org.jadira.bindings.core.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jadira.bindings.core.annotation.BindingScope;
import org.jadira.bindings.core.annotation.DefaultBinding;
import org.jadira.bindings.core.binder.ConverterKey;
import org.jadira.bindings.core.spi.ConverterProvider;
import org.jadira.bindings.core.utils.reflection.TypeHelper;

/**
 * Used to implement a binding provider instance that uses annotations to match methods 
 * @param <T> The annotation used to identify a to method
 * @param <F> The annotation used to identify a from method
 */
public class AbstractAnnotationMatchingConverterProvider<T extends Annotation, F extends Annotation> implements ConverterProvider  {

	public <I,O> Map<ConverterKey<?, ?>, Method> matchToMethods(Class<?> cls) {

		Map<ConverterKey<?, ?>, Method> matchedMethods = new HashMap<ConverterKey<?, ?>, Method>();
		
		@SuppressWarnings("unchecked")
		Class<T> toAnnotation = (Class<T>) TypeHelper.getTypeArguments(
				AbstractAnnotationMatchingConverterProvider.class,
				this.getClass()).get(0);

		Class<?> loopCls = cls;

		while (loopCls != Object.class) {
			Method[] methods = loopCls.getDeclaredMethods();
			for (Method method : methods) {
				if (signatureIndicatesToMethodCandidate(method)) {
					T toMethodAnnotation = method.getAnnotation(toAnnotation);
					if (toMethodAnnotation != null) {
						List<Class<? extends Annotation>> qualifiers = determineQualifiers(toMethodAnnotation, method.getAnnotations());
						
						for (Class<? extends Annotation> nextQualifier : qualifiers) {
							@SuppressWarnings("unchecked")
							Class<O> returnType = (Class<O>)method.getReturnType();
						
							
							if (Modifier.isStatic(method.getModifiers())) {
								@SuppressWarnings("unchecked")
								Class<I> inputClass = (Class<I>) method.getParameterTypes()[0];
								matchedMethods.put(new ConverterKey<I,O>(inputClass, returnType, nextQualifier), method);
							} else {
								@SuppressWarnings("unchecked")
								Class<I> inputClass = (Class<I>) cls;
								matchedMethods.put(new ConverterKey<I,O>(inputClass, returnType, nextQualifier), method);								
							}
							
						}
					}
				}
			}
			loopCls = loopCls.getSuperclass();
		}
		return matchedMethods;
	}

	private boolean signatureIndicatesToMethodCandidate(Method method) {
		
		if (!Modifier.isPublic(method.getModifiers())) {
			return false;
		}
		if (method.getReturnType().equals(Void.TYPE)) {
			return false;
		}
		if (!isToMatch(method)) {
			return false;
		}
		if ((!(Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 1))
			&&
			(!(!Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 0))) {
			return false;
		}
		return true;
	}

	public <I,O> Map<ConverterKey<?,?>, Constructor<O>> matchFromConstructors(Class<O> cls) {

		Map<ConverterKey<?, ?>, Constructor<O>> matchedConstructors = new HashMap<ConverterKey<?, ?>, Constructor<O>>();
		
		@SuppressWarnings("unchecked")
		Class<F> fromAnnotation = (Class<F>) TypeHelper.getTypeArguments(
				AbstractAnnotationMatchingConverterProvider.class,
				this.getClass()).get(1);

		Class<?> loopCls = cls;

		while (loopCls != Object.class) {
			
			@SuppressWarnings("unchecked")
			Constructor<O>[] constructors = (Constructor<O>[])loopCls.getDeclaredConstructors();
			
			for (Constructor<O> constructor : constructors) {
				if (signatureIndicatesFromConstructorCandidate(constructor)) {
					F fromConstructorAnnotation = constructor.getAnnotation(fromAnnotation);
					if (fromConstructorAnnotation != null) {
						List<Class<? extends Annotation>> qualifiers = determineQualifiers(fromConstructorAnnotation, constructor.getAnnotations());
						
						for (Class<? extends Annotation> nextQualifier : qualifiers) {
							@SuppressWarnings("unchecked")
							Class<I> paramType = (Class<I>)constructor.getParameterTypes()[0];
							matchedConstructors.put(new ConverterKey<I,O>((Class<I>)paramType, cls, nextQualifier), constructor);
						}
					}
				}
			}
			loopCls = loopCls.getSuperclass();
		}
		return matchedConstructors;
	}
	
	private boolean signatureIndicatesFromConstructorCandidate(Constructor<?> constructor) {
		
		if (!Modifier.isPublic(constructor.getModifiers())) {
			return false;
		}
		if (!(constructor.getParameterTypes().length == 1)) {
			return false;
		}
		if (!isFromMatch(constructor)) {
			return false;
		}
		return true;
	}

	public <I,O> Map<ConverterKey<?,?>, Method> matchFromMethods(Class<?> cls) {

		Map<ConverterKey<?, ?>, Method> matchedMethods = new HashMap<ConverterKey<?, ?>, Method>();
		
		@SuppressWarnings("unchecked")
		Class<F> fromAnnotation = (Class<F>) TypeHelper.getTypeArguments(
				AbstractAnnotationMatchingConverterProvider.class,
				this.getClass()).get(1);

		Class<?> loopCls = cls;

		while (loopCls != Object.class) {
			Method[] methods = loopCls.getDeclaredMethods();
			for (Method method : methods) {
				if (signatureIndicatesFromMethodCandidate(method)) {
					
					F fromMethodAnnotation = method.getAnnotation(fromAnnotation);
					if (fromMethodAnnotation != null) {
						List<Class<? extends Annotation>> qualifiers = determineQualifiers(fromMethodAnnotation, method.getAnnotations());
						
						for (Class<? extends Annotation> nextQualifier : qualifiers) {
							@SuppressWarnings("unchecked")
							Class<I> paramType = (Class<I>)method.getParameterTypes()[0];
							@SuppressWarnings("unchecked")
							Class<O> outputClass = (Class<O>)cls;
							matchedMethods.put(new ConverterKey<I,O>(paramType, outputClass, nextQualifier), method);
						}
					}
				}
			}
			loopCls = loopCls.getSuperclass();
		}
		return matchedMethods;
	}

	private boolean signatureIndicatesFromMethodCandidate(Method method) {
	
		if (!Modifier.isPublic(method.getModifiers())) {
			return false;
		}
		if (method.getReturnType().equals(Void.TYPE)) {
			return false;
		}
		if (!(Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 1)) {
			return false;
		}
		if (!isFromMatch(method)) {
			return false;
		}
		return true;
	}

	/**
	 * Subclasses can override this template method with their own matching strategy
	 * @param method The method to be determined
	 * @return True if match
	 */
	protected boolean isToMatch(Method method) {
		return true;
	}

	/**
	 * Subclasses can override this template method with their own matching strategy
	 * @param method The constructor to be determined
	 * @return True if match
	 */
	protected boolean isFromMatch(Constructor<?> constructor) {
		return true;
	}

	/**
	 * Subclasses can override this template method with their own matching strategy
	 * @param method The method to be determined
	 * @return True if match
	 */
	protected boolean isFromMatch(Method method) {
		return true;
	}
	
	/**
     * Returns the qualifiers for this method, setting the Default qualifier if none are found
     * Qualifiers can be either explicitly applied to the method, or implicit on account of being found
     * in the annotation itself (for example @Plus would have a default binding scope of @PlusOperator)
     * @param annotations Array of Annotations on the target method
     * @return True if method can be matched for the given scope
     */
    protected List<Class<? extends Annotation>> determineQualifiers(Annotation bindingAnnotation, Annotation... allAnnotations) {

        List<Class<? extends Annotation>> result = new ArrayList<Class<? extends Annotation>>();

        // The binding annotation itself is marked with @BindingScope
        Class<? extends Annotation> bindingAnnotationType = bindingAnnotation.annotationType();
        if (bindingAnnotationType.getAnnotation(BindingScope.class) != null) {
            result.add(bindingAnnotationType);
        }

        // The binding annotation is annotated with annotations marked with @BindingScope
        for (Annotation next : bindingAnnotation.annotationType().getAnnotations()) {
            Class<? extends Annotation> nextType = next.annotationType();
            if (nextType.getAnnotation(BindingScope.class) != null) {
                result.add(nextType);
            }
        }

        // The method used to attach the annotation is annotated with annotations marked with @BindingScope
        for (Annotation next : allAnnotations) {
            Class<? extends Annotation> nextType = next.annotationType();
            if (nextType.getAnnotation(BindingScope.class) != null) {
                result.add(nextType);
            }
        }

        if (result.isEmpty()) {
        	result.add(DefaultBinding.class);
        }
        
        return result;
    }

}
