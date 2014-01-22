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

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.access.api.FieldAccess;
import org.jadira.reflection.access.classloader.AccessClassLoader;
import org.jadira.reflection.access.portable.PortableFieldAccess;
import org.jadira.reflection.core.misc.ClassUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * ClassAccess implementation which uses ASM and the invokeDynamic instruction. 
 * InvokeDynamic requests are accessed using a caching callpoint (via Dynalang) which means performance is
 * similar to standard ASM based access
 * @param <C> The Class to be accessed
 */
public abstract class InvokeDynamicClassAccess<C> implements ClassAccess<C> {

    private static final ConcurrentHashMap<Class<?>, InvokeDynamicClassAccess<?>> CLASS_ACCESSES = new ConcurrentHashMap<Class<?>, InvokeDynamicClassAccess<?>>();
    
    private static final String CLASS_ACCESS_NM = ClassAccess.class.getName().replace('.', '/');

    private static final String INVOKEDYNAMIC_CLASS_ACCESS_NM = InvokeDynamicClassAccess.class.getName().replace('.', '/');
 
	/**
	 * The class to be accessed
	 */
    protected Class<C> clazz;

	/**
	 * An ordered array giving the names of the fields in the class to accessed
	 */
    protected final String[] fieldNames;
    
	/**
	 * An ordered array giving the Fields in the class to accessed
	 */
    protected final Field[] fields;

    private final FieldAccess<C>[] fieldAccess;

	/**
	 * An ordered array giving the names of the methods in the class to accessed
	 */
    protected final String[] methodNames;
    
	/**
	 * An ordered array giving the Methods in the class to accessed
	 */
    protected final Method[] methods;

    private final InvokeDynamicMethodAccess<C>[] methodAccess;
    
    private boolean isNonStaticMemberClass;
    
	/**
	 * Indicates if the class being accessed is a non-static member class
	 * @return True if the class is a non-static member class
	 */
    public boolean isNonStaticMemberClass() {
        return isNonStaticMemberClass;
    }

	/**
	 * Constructor, intended for use by generated subclasses
	 * @param clazz The Class to be accessed
	 */
    @SuppressWarnings("unchecked")
    protected InvokeDynamicClassAccess(Class<C> clazz) {
        this.clazz = clazz;

        Field[] myFields = ClassUtils.collectInstanceFields(clazz);

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
                    if ((myFields[i].getModifiers() & Modifier.PRIVATE) != 0) {
                        myFieldAccess[tIdx] = PortableFieldAccess.get(myFields[i]);
                    } else {
                        myFieldAccess[tIdx] = InvokeDynamicFieldAccess.get(this, myFields[i]);
                    }
                    fields[tIdx] = myFields[i];
                    break;
                }
            }

        }
        fieldAccess = myFieldAccess;
        
        Method[] myMethods = ClassUtils.collectMethods(clazz);
        
        String[] unsortedMethodNames = new String[myMethods.length];
        for (int i=0; i < unsortedMethodNames.length; i++) {
            unsortedMethodNames[i] = myMethods[i].getName();
        }
        methodNames = Arrays.copyOf(unsortedMethodNames, unsortedMethodNames.length);
        Arrays.sort(methodNames);
        
        final InvokeDynamicMethodAccess<C>[] myMethodAccess = (InvokeDynamicMethodAccess<C>[])new InvokeDynamicMethodAccess[myMethods.length];
        methods = new Method[myMethods.length];
        
        for (int i=0; i < methods.length; i++) {
   
            String methodName = unsortedMethodNames[i];
            for (int tIdx = 0; tIdx < unsortedMethodNames.length; tIdx++) {
                if (methodName.equals(methodNames[tIdx])) {
                    myMethodAccess[tIdx] = InvokeDynamicMethodAccess.get(myMethods[i]);
                    methods[tIdx] = myMethods[i];
                    break;
                }
            }
            
        }
        methodAccess = myMethodAccess;
    }

    @Override
    public Class<C> getType() {
        return clazz;
    }

    @Override
    public FieldAccess<C>[] getFieldAccessors() {
        return fieldAccess;
    }

    @Override
    public FieldAccess<C> getFieldAccess(Field f) {
        int idx = Arrays.binarySearch(fieldNames, f.getName());
        return fieldAccess[idx];
    }
    
	/**
	 * Get a new instance that can access the given Class. If the ClassAccess for this class
	 * has not been obtained before, then the specific InvokeDynamicClassAccess is created by 
	 * generating a specialised subclass of this class and returning it. 
	 * @param clazz Class to be accessed
	 * @return New InvokeDynamicClassAccess instance
	 */
    public static <C> InvokeDynamicClassAccess<C> get(Class<C> clazz) {

        @SuppressWarnings("unchecked")
        InvokeDynamicClassAccess<C> access = (InvokeDynamicClassAccess<C>) CLASS_ACCESSES.get(clazz);
        if (access != null) {
            return access;
        }
        
        Class<?> enclosingType = clazz.getEnclosingClass();

        final boolean isNonStaticMemberClass = determineNonStaticMemberClass(clazz, enclosingType);

        String clazzName = clazz.getName();

        String accessClassName = constructAccessClassName(clazzName);

        Class<?> accessClass = null;

        AccessClassLoader loader = AccessClassLoader.get(clazz);
        synchronized (loader) {
            try {
                accessClass = loader.loadClass(accessClassName);
            } catch (ClassNotFoundException ignored) {

                String accessClassNm = accessClassName.replace('.', '/');
                String clazzNm = clazzName.replace('.', '/');
                String enclosingClassNm = determineEnclosingClassNm(clazz, enclosingType, isNonStaticMemberClass);

                String signatureString = "L" + INVOKEDYNAMIC_CLASS_ACCESS_NM + "<L" + clazzNm + ";>;L" + CLASS_ACCESS_NM + "<L" + clazzNm + ";>;";

                ClassWriter cw = new ClassWriter(0);

//              TraceClassVisitor tcv = new TraceClassVisitor(cv, new PrintWriter(System.err));
//              CheckClassAdapter cw = new CheckClassAdapter(tcv);

                cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, accessClassNm, signatureString, INVOKEDYNAMIC_CLASS_ACCESS_NM, null);

                enhanceForConstructor(cw, accessClassNm, clazzNm);

                if (isNonStaticMemberClass) {
                    enhanceForNewInstanceInner(cw, clazzNm, enclosingClassNm);
                } else {
                    enhanceForNewInstance(cw, clazzNm);
                }

                cw.visitEnd();

                loader.registerClass(accessClassName, cw.toByteArray());

                try {
                    accessClass = loader.findClass(accessClassName);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("AccessClass unexpectedly could not be found", e);
                }
            }
        }
        try {
            @SuppressWarnings("unchecked")
            Constructor<InvokeDynamicClassAccess<C>> c = (Constructor<InvokeDynamicClassAccess<C>>) accessClass.getConstructor(new Class[] { Class.class });
            access = c.newInstance(clazz);
            access.isNonStaticMemberClass = isNonStaticMemberClass;
            
            CLASS_ACCESSES.putIfAbsent(clazz, access);
            return access;
        } catch (Exception ex) {
            throw new RuntimeException("Error constructing constructor access class: " + accessClassName + "{ " + ex.getMessage() + " }", ex);
        }
    }

    private static boolean determineNonStaticMemberClass(Class<?> clazz, Class<?> enclosingType) {
        final boolean isNonStaticMemberClass;
        if (enclosingType != null && clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {
            isNonStaticMemberClass = true;
        } else {
            isNonStaticMemberClass = false;
        }
        ;
        return isNonStaticMemberClass;
    }
    
    private static <C> String determineEnclosingClassNm(Class<C> clazz, Class<?> enclosingType, final boolean isNonStaticMemberClass) {

        final String enclosingClassNm;

        if (!isNonStaticMemberClass) {

            enclosingClassNm = null;
            try {
                clazz.getConstructor((Class[]) null);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Class does not have a no-arg constructor" + clazz.getName());
            }

        } else {

            enclosingClassNm = enclosingType.getName().replace('.', '/');
            try {
                clazz.getConstructor(enclosingType); // Inner classes should have this.
            } catch (Exception ex) {
                throw new IllegalArgumentException("Inner Class does not have a no-arg constructor" + clazz.getName());
            }
        }
        return enclosingClassNm;
    }
    
    private static String constructAccessClassName(String clazzName) {

        String accessClassName = clazzName + InvokeDynamicClassAccess.class.getSimpleName();
        if (accessClassName.startsWith("java.")) {
            accessClassName = InvokeDynamicClassAccess.class.getSimpleName().toLowerCase() + accessClassName;
        }

        return accessClassName;
    }
    
    private static void enhanceForConstructor(ClassVisitor cw, String accessClassNm, String clazzNm) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Class;)V", "(L" + clazzNm + ";)V", null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, INVOKEDYNAMIC_CLASS_ACCESS_NM, "<init>", "(Ljava/lang/Class;)V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private static void enhanceForNewInstance(ClassVisitor cw, String classNm) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);

        mv.visitCode();
        mv.visitTypeInsn(NEW, classNm);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, classNm, "<init>", "()V");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    private static void enhanceForNewInstanceInner(ClassVisitor cw, String classNm, String enclosingClassNm) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()LLjava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, classNm);
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, enclosingClassNm);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, enclosingClassNm, "<init>", "()V");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKEVIRTUAL, classNm, "getClass", "()Ljava/lang/Class;");
        mv.visitInsn(POP);
        mv.visitMethodInsn(INVOKESPECIAL, classNm, "<init>", "(L" + enclosingClassNm + ";)V");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(4, 1);
        mv.visitEnd();
    }
    
    @Override
    public abstract C newInstance();

    @Override
    public InvokeDynamicMethodAccess<C>[] getMethodAccessors() {
        return methodAccess;
    }
    
    @Override
    public InvokeDynamicMethodAccess<C> getMethodAccess(Method m) {
        int idx = Arrays.binarySearch(methodNames, m.getName());
        return methodAccess[idx];
    }
 }
