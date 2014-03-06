package org.jadira.reflection.access.asm;

import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.access.api.ClassAccessFactory;

public class AsmClassAccessFactory implements ClassAccessFactory {

	public static final ClassAccessFactory FACTORY = new AsmClassAccessFactory();
	
	private AsmClassAccessFactory() {
	}
	
	@Override
	public <C> ClassAccess<C> getClassAccess(Class<C> clazz) {
		return AsmClassAccess.get(clazz);
	}
	
	public static ClassAccessFactory get() {
		return FACTORY;
	}
}