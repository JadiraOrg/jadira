package org.jadira.reflection.access.unsafe;

import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.access.api.ClassAccessFactory;

public class UnsafeClassAccessFactory implements ClassAccessFactory {

	public static final ClassAccessFactory FACTORY = new UnsafeClassAccessFactory();
	
	private UnsafeClassAccessFactory() {
	}
	
	@Override
	public <C> ClassAccess<C> getClassAccess(Class<C> clazz) {
		return UnsafeClassAccess.get(clazz);
	}
	
	public static ClassAccessFactory get() {
		return FACTORY;
	}
}
