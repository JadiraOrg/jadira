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

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DRETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FRETURN;
import static org.objectweb.asm.Opcodes.F_SAME;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LRETURN;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;
import static org.objectweb.asm.Type.BOOLEAN;
import static org.objectweb.asm.Type.BYTE;
import static org.objectweb.asm.Type.CHAR;
import static org.objectweb.asm.Type.DOUBLE;
import static org.objectweb.asm.Type.FLOAT;
import static org.objectweb.asm.Type.INT;
import static org.objectweb.asm.Type.LONG;
import static org.objectweb.asm.Type.SHORT;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * ClassAccess implementation which uses ASM to generate accessors
 * @param <C> The Class to be accessed
 */
public abstract class AsmClassAccess<C> implements ClassAccess<C> {

    private static final ConcurrentHashMap<Class<?>, AsmClassAccess<?>> CLASS_ACCESSES = new ConcurrentHashMap<Class<?>, AsmClassAccess<?>>();
    
	private static final String CLASS_ACCESS_NM = ClassAccess.class.getName().replace('.', '/');

	private static final String ASM_CLASS_ACCESS_NM = AsmClassAccess.class.getName().replace('.', '/');

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
	protected AsmClassAccess(Class<C> clazz) {
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
						myFieldAccess[tIdx] = AsmFieldAccess.get(this, myFields[i]);
					}
					fields[tIdx] = myFields[i];
					break;
				}
			}

		}
		fieldAccess = myFieldAccess;
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
	 * has not been obtained before, then the specific AsmClassAccess is created by generating
	 * a specialised subclass of this class and returning it. 
	 * @param clazz Class to be accessed
	 * @return New AsmClassAccess instance
	 */
	public static <C> AsmClassAccess<C> get(Class<C> clazz) {

	    @SuppressWarnings("unchecked")
	    AsmClassAccess<C> access = (AsmClassAccess<C>) CLASS_ACCESSES.get(clazz);
        if (access != null) {
            return access;
        }
        	    
		Class<?> enclosingType = clazz.getEnclosingClass();

		final boolean isNonStaticMemberClass = determineNonStaticMemberClass(clazz, enclosingType);

		String clazzName = clazz.getName();
		Field[] fields = ClassUtils.collectInstanceFields(clazz, false, false, true);

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

				String signatureString = "L" + ASM_CLASS_ACCESS_NM + "<L" + clazzNm + ";>;L" + CLASS_ACCESS_NM + "<L" + clazzNm + ";>;";

				ClassWriter cw = new ClassWriter(0);

//				TraceClassVisitor tcv = new TraceClassVisitor(cv, new PrintWriter(System.err));
//				CheckClassAdapter cw = new CheckClassAdapter(tcv);

				cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, accessClassNm, signatureString, ASM_CLASS_ACCESS_NM, null);

				enhanceForConstructor(cw, accessClassNm, clazzNm);

				if (isNonStaticMemberClass) {
					enhanceForNewInstanceInner(cw, clazzNm, enclosingClassNm);
				} else {
					enhanceForNewInstance(cw, clazzNm);
				}

				enhanceForGetValueObject(cw, accessClassNm, clazzNm, fields);
				enhanceForPutValueObject(cw, accessClassNm, clazzNm, fields);
				enhanceForGetValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.BOOLEAN_TYPE);
				enhanceForPutValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.BOOLEAN_TYPE);
				enhanceForGetValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.BYTE_TYPE);
				enhanceForPutValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.BYTE_TYPE);
				enhanceForGetValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.SHORT_TYPE);
				enhanceForPutValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.SHORT_TYPE);
				enhanceForGetValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.INT_TYPE);
				enhanceForPutValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.INT_TYPE);
				enhanceForGetValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.LONG_TYPE);
				enhanceForPutValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.LONG_TYPE);
				enhanceForGetValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.DOUBLE_TYPE);
				enhanceForPutValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.DOUBLE_TYPE);
				enhanceForGetValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.FLOAT_TYPE);
				enhanceForPutValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.FLOAT_TYPE);
				enhanceForGetValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.CHAR_TYPE);
				enhanceForPutValuePrimitive(cw, accessClassNm, clazzNm, fields, Type.CHAR_TYPE);
				// enhanceForInvoke

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
			Constructor<AsmClassAccess<C>> c = (Constructor<AsmClassAccess<C>>) accessClass.getConstructor(new Class[] { Class.class });
			
			access = c.newInstance(clazz);
			access.isNonStaticMemberClass = isNonStaticMemberClass;
				        
			CLASS_ACCESSES.putIfAbsent(clazz, access);
			
			return access;
		} catch (Exception ex) {
		    throw new RuntimeException("Error constructing constructor access class: " + accessClassName + "{ " + ex.getMessage() + " }", ex);
		}
	}

	private static Label[] constructLabels(Field[] fields) {

		Label[] labels = new Label[fields.length];
		for (int i = 0, n = labels.length; i < n; i++) {
			labels[i] = new Label();
		}
		return labels;
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

	private static String constructAccessClassName(String clazzName) {

		String accessClassName = clazzName + AsmClassAccess.class.getSimpleName();
		if (accessClassName.startsWith("java.")) {
			accessClassName = AsmClassAccess.class.getSimpleName().toLowerCase() + accessClassName;
		}

		return accessClassName;
	}

	private static void enhanceForConstructor(ClassVisitor cw, String accessClassNm, String clazzNm) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Class;)V", "(L" + clazzNm + ";)V", null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKESPECIAL, ASM_CLASS_ACCESS_NM, "<init>", "(Ljava/lang/Class;)V");
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

	private static void enhanceForGetValueObject(ClassVisitor cw, String accessClassNm, String clazzNm, Field[] fields) {

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "getValue", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", null, null);

		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, accessClassNm, "fieldNames", "[Ljava/lang/String;");
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "binarySearch", "([Ljava/lang/Object;Ljava/lang/Object;)I");
		mv.visitVarInsn(ISTORE, 3);
		mv.visitVarInsn(ILOAD, 3);

		final int maxStack;

		if (fields.length > 0) {
			maxStack = 5;
			Label[] labels = constructLabels(fields);

			Label defaultLabel = new Label();
			mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

			for (int i = 0, n = labels.length; i < n; i++) {
				Field field = fields[i];
				mv.visitLabel(labels[i]);
				mv.visitFrame(F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, clazzNm);
				mv.visitFieldInsn(GETFIELD, clazzNm, field.getName(), Type.getDescriptor(field.getType()));

				Type fieldType = Type.getType(field.getType());
				switch (fieldType.getSort()) {
				case Type.BOOLEAN:
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
					break;
				case Type.BYTE:
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
					break;
				case Type.CHAR:
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
					break;
				case Type.SHORT:
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
					break;
				case Type.INT:
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
					break;
				case Type.FLOAT:
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
					break;
				case Type.LONG:
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
					break;
				case Type.DOUBLE:
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
					break;
				}

				mv.visitInsn(ARETURN);
			}

			mv.visitLabel(defaultLabel);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		} else {
			maxStack = 6;
		}
		enhanceForThrowingException(mv, IllegalArgumentException.class, "Field was not found", "Ljava/lang/Object;", ALOAD, 2);
		mv.visitMaxs(maxStack, 4);
		mv.visitEnd();
	}

	private static void enhanceForPutValueObject(ClassVisitor cw, String accessClassNm, String clazzNm, Field[] fields) {

		int maxStack = 6;
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "putValue", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V", null, null);

		mv.visitCode();

		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, accessClassNm, "fieldNames", "[Ljava/lang/String;");
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "binarySearch", "([Ljava/lang/Object;Ljava/lang/Object;)I");
		mv.visitVarInsn(ISTORE, 4);
		mv.visitVarInsn(ILOAD, 4);

		if (fields.length > 0) {
			maxStack = 5;
			Label[] labels = constructLabels(fields);

			Label defaultLabel = new Label();
			mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

			for (int i = 0, n = labels.length; i < n; i++) {
				Field field = fields[i];

				mv.visitLabel(labels[i]);
				mv.visitFrame(F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, clazzNm);

				mv.visitVarInsn(ALOAD, 3);

				Type fieldType = Type.getType(field.getType());
				switch (fieldType.getSort()) {
				case Type.BOOLEAN:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
					break;
				case Type.BYTE:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
					break;
				case Type.CHAR:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
					break;
				case Type.SHORT:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
					break;
				case Type.INT:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
					break;
				case Type.FLOAT:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
					break;
				case Type.LONG:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
					break;
				case Type.DOUBLE:
					mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
					break;
				case Type.ARRAY:
					mv.visitTypeInsn(CHECKCAST, fieldType.getDescriptor());
					break;
				case Type.OBJECT:
					mv.visitTypeInsn(CHECKCAST, fieldType.getInternalName());
					break;
				}

				mv.visitFieldInsn(PUTFIELD, clazzNm, field.getName(), fieldType.getDescriptor());
				mv.visitInsn(RETURN);
			}

			mv.visitLabel(defaultLabel);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		} else {
			maxStack = 6;
		}
		enhanceForThrowingException(mv, IllegalArgumentException.class, "Field was not found", "Ljava/lang/Object;", ALOAD, 2);
		mv.visitMaxs(maxStack, 5);
		mv.visitEnd();
	}

	private static void enhanceForPutValuePrimitive(ClassVisitor cw, String accessClassNm, String clazzNm, Field[] fields, Type type) {

		final String methodName;
		final String typeNm = type.getDescriptor();
		final int instruction;

		switch (type.getSort()) {
		case BOOLEAN:
			methodName = "putBooleanValue";
			instruction = ILOAD;
			break;
		case BYTE:
			methodName = "putByteValue";
			instruction = ILOAD;
			break;
		case CHAR:
			methodName = "putCharValue";
			instruction = ILOAD;
			break;
		case SHORT:
			methodName = "putShortValue";
			instruction = ILOAD;
			break;
		case INT:
			methodName = "putIntValue";
			instruction = ILOAD;
			break;
		case FLOAT:
			methodName = "putFloatValue";
			instruction = FLOAD;
			break;
		case LONG:
			methodName = "putLongValue";
			instruction = LLOAD;
			break;
		case DOUBLE:
			methodName = "putDoubleValue";
			instruction = DLOAD;
			break;
		default:
			methodName = "put" + type.getInternalName().lastIndexOf('/') + "Value";
			instruction = ALOAD;
			break;
		}

		int offset = (instruction == LLOAD || instruction == DLOAD) ? 1 : 0;

		int maxStack = 6;
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "(Ljava/lang/Object;Ljava/lang/String;" + typeNm + ")V", null, null);

		mv.visitCode();

		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, accessClassNm, "fieldNames", "[Ljava/lang/String;");
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "binarySearch", "([Ljava/lang/Object;Ljava/lang/Object;)I");

		mv.visitVarInsn(ISTORE, 4 + offset);
		mv.visitVarInsn(ILOAD, 4 + offset);

		if (fields.length > 0) {
			maxStack = 6;

			Label[] labels = new Label[fields.length];
			Label labelForInvalidTypes = new Label();
			boolean hasAnyBadTypeLabel = false;

			for (int i = 0, n = labels.length; i < n; i++) {
				if (Type.getType(fields[i].getType()).equals(type))
					labels[i] = new Label();
				else {
					labels[i] = labelForInvalidTypes;
					hasAnyBadTypeLabel = true;
				}
			}
			Label defaultLabel = new Label();
			mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

			for (int i = 0, n = labels.length; i < n; i++) {
				if (!labels[i].equals(labelForInvalidTypes)) {
					Field field = fields[i];

					mv.visitLabel(labels[i]);
					mv.visitFrame(F_SAME, 0, null, 0, null);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitTypeInsn(CHECKCAST, clazzNm);

					mv.visitVarInsn(instruction, 3);
					mv.visitFieldInsn(PUTFIELD, clazzNm, field.getName(), typeNm);
					mv.visitInsn(RETURN);
				}
			}

			if (hasAnyBadTypeLabel) {
				mv.visitLabel(labelForInvalidTypes);
				mv.visitFrame(F_SAME, 0, null, 0, null);
				enhanceForThrowingException(mv, IllegalArgumentException.class, type.getClassName(), typeNm, instruction, 3);
			}

			mv.visitLabel(defaultLabel);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		}

		final int maxLocals = 5 + offset;

		enhanceForThrowingException(mv, IllegalArgumentException.class, "Field was not found", typeNm, instruction, 3);
		mv.visitMaxs(maxStack, maxLocals);
		mv.visitEnd();
	}

	private static void enhanceForGetValuePrimitive(ClassVisitor cw, String accessClassNm, String clazzNm, Field[] fields, Type type) {

		String methodName;
		final String typeNm = type.getDescriptor();
		final int instruction;

		switch (type.getSort()) {
		case Type.BOOLEAN:
			methodName = "getBooleanValue";
			instruction = IRETURN;
			break;
		case Type.BYTE:
			methodName = "getByteValue";
			instruction = IRETURN;
			break;
		case Type.CHAR:
			methodName = "getCharValue";
			instruction = IRETURN;
			break;
		case Type.SHORT:
			methodName = "getShortValue";
			instruction = IRETURN;
			break;
		case Type.INT:
			methodName = "getIntValue";
			instruction = IRETURN;
			break;
		case Type.FLOAT:
			methodName = "getFloatValue";
			instruction = FRETURN;
			break;
		case Type.LONG:
			methodName = "getLongValue";
			instruction = LRETURN;
			break;
		case Type.DOUBLE:
			methodName = "getDoubleValue";
			instruction = DRETURN;
			break;
		default:
			methodName = "getValue";
			instruction = ARETURN;
			break;
		}

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "(Ljava/lang/Object;Ljava/lang/String;)" + typeNm, null, null);

		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, accessClassNm, "fieldNames", "[Ljava/lang/String;");
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "binarySearch", "([Ljava/lang/Object;Ljava/lang/Object;)I");
		mv.visitVarInsn(ISTORE, 3);
		mv.visitVarInsn(ILOAD, 3);

		final int maxStack;

		if (fields.length > 0) {
			maxStack = 5;
			Label[] labels = constructLabels(fields);

			Label defaultLabel = new Label();
			mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

			for (int i = 0, n = labels.length; i < n; i++) {
				Field field = fields[i];
				mv.visitLabel(labels[i]);
				mv.visitFrame(F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, clazzNm);
				mv.visitFieldInsn(GETFIELD, clazzNm, field.getName(), typeNm);
				mv.visitInsn(instruction);
			}

			mv.visitLabel(defaultLabel);
			mv.visitFrame(F_SAME, 0, null, 0, null);
		} else {
			maxStack = 6;
		}
		enhanceForThrowingException(mv, IllegalArgumentException.class, "Field was not found", "Ljava/lang/Object;", ALOAD, 2);
		mv.visitMaxs(maxStack, 4);
		mv.visitEnd();
	}

	private static void enhanceForThrowingException(MethodVisitor mv, Class<? extends Exception> exceptionClass, String msg, String argType, int instruction, int slot) {

		String exceptionClassNm = Type.getInternalName(exceptionClass);
		mv.visitTypeInsn(NEW, exceptionClassNm);
		mv.visitInsn(DUP);
		mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
		mv.visitInsn(DUP);
		mv.visitLdcInsn(msg);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
		mv.visitVarInsn(instruction, slot);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(" + argType + ")Ljava/lang/StringBuilder;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESPECIAL, exceptionClassNm, "<init>", "(Ljava/lang/String;)V");
		mv.visitInsn(ATHROW);
	}

	@Override
	public abstract C newInstance();

	/**
	 * Retrieve the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @return The field value as an object
	 */
	public abstract Object getValue(C object, String fieldName);

	/**
	 * Update the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @param value The new value
	 */
	public abstract void putValue(C object, String fieldName, Object value);

	/**
	 * Retrieve the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @return The field value as a boolean
	 */
	public abstract boolean getBooleanValue(C object, String fieldName);

	/**
	 * Update the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @param value The new boolean value
	 */
	public abstract void putBooleanValue(C object, String fieldName, boolean value);

	/**
	 * Retrieve the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @return The field value as a byte
	 */
	public abstract byte getByteValue(C object, String fieldName);

	/**
	 * Update the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @param value The new byte value
	 */
	public abstract void putByteValue(C object, String fieldName, byte value);

	/**
	 * Retrieve the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @return The field value as a char
	 */
	public abstract char getCharValue(C object, String fieldName);

	/**
	 * Update the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @param value The new char value
	 */
	public abstract void putCharValue(C object, String fieldName, char value);

	/**
	 * Retrieve the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @return The field value as a short
	 */
	public abstract short getShortValue(C object, String fieldName);

	/**
	 * Update the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @param value The new short value
	 */
	public abstract void putShortValue(C object, String fieldName, short value);

	/**
	 * Retrieve the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @return The field value as a int
	 */
	public abstract int getIntValue(C object, String fieldName);

	/**
	 * Update the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @param value The new int value
	 */
	public abstract void putIntValue(C object, String fieldName, int value);

	/**
	 * Retrieve the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @return The field value as a long
	 */
	public abstract long getLongValue(C object, String fieldName);

	/**
	 * Update the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @param value The new long value
	 */
	public abstract void putLongValue(C object, String fieldName, long value);

	/**
	 * Retrieve the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @return The field value as a float
	 */
	public abstract float getFloatValue(C object, String fieldName);

	/**
	 * Update the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @param value The new float value
	 */
	public abstract void putFloatValue(C object, String fieldName, float value);

	/**
	 * Retrieve the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @return The field value as a double
	 */
	public abstract double getDoubleValue(C object, String fieldName);

	/**
	 * Update the value of the field for the given instance
	 * @param object The instance to access the field for
	 * @param fieldName Name of the field to access
	 * @param value The new double value
	 */
	public abstract void putDoubleValue(C object, String fieldName, double value);
}
