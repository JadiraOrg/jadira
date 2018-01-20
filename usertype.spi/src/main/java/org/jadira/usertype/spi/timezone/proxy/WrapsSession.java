package org.jadira.usertype.spi.timezone.proxy;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface WrapsSession {

	public SharedSessionContractImplementor wrapSession(SharedSessionContractImplementor target);
}
