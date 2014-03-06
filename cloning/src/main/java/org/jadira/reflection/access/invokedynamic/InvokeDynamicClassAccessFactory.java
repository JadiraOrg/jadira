package org.jadira.reflection.access.invokedynamic;

import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.access.api.ClassAccessFactory;

public class InvokeDynamicClassAccessFactory implements ClassAccessFactory {

	public static final ClassAccessFactory FACTORY = new InvokeDynamicClassAccessFactory();
	
	private InvokeDynamicClassAccessFactory() {
	}
	
	@Override
	public <C> ClassAccess<C> getClassAccess(Class<C> clazz) {
		return InvokeDynamicClassAccess.get(clazz);
	}
	
	public static ClassAccessFactory get() {
		return FACTORY;
	}
}
