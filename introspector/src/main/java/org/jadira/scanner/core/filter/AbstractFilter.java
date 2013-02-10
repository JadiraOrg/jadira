package org.jadira.scanner.core.filter;

import org.jadira.scanner.core.api.Filter;
import org.jadira.scanner.core.spi.TypeHelper;

public abstract class AbstractFilter<E> implements Filter<E> {

	@Override
	public Class<E> targetType() {
		@SuppressWarnings("unchecked")
		final Class<E> result = (Class<E>)TypeHelper.getTypeArguments(AbstractFilter.class, this.getClass()).get(0);
		return result;
	}
}
