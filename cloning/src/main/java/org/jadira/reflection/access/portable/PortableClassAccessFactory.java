package org.jadira.reflection.access.portable;

import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.access.api.ClassAccessFactory;

public class PortableClassAccessFactory implements ClassAccessFactory {

	public static final ClassAccessFactory FACTORY = new PortableClassAccessFactory();
	
	private PortableClassAccessFactory() {
	}
	
	@Override
	public <C> ClassAccess<C> getClassAccess(Class<C> clazz) {
		return PortableClassAccess.get(clazz);
	}
	
	public static ClassAccessFactory get() {
		return FACTORY;
	}
}

