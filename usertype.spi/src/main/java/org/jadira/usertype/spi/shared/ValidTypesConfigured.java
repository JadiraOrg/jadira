package org.jadira.usertype.spi.shared;

import java.util.List;

public interface ValidTypesConfigured<T> {

	void setValidTypes(List<Class<T>> types);

	public List<Class<T>> getValidTypes();
}
