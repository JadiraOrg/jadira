package org.jadira.usertype.spi.timezone.proxy;

import java.util.TimeZone;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.jadira.usertype.spi.engine.AbstractProxySharedSessionContractImplementor;

public class TimeZoneProvidingSharedSessionContractImplementor extends AbstractProxySharedSessionContractImplementor {

	private static final long serialVersionUID = -120329888642773756L;
	private TimeZone timezone;

	public TimeZoneProvidingSharedSessionContractImplementor(SharedSessionContractImplementor target, TimeZone timezone) {
		super(target);
		this.timezone = timezone;
	}
	
	@Override
	public TimeZone getJdbcTimeZone() {
		if (timezone == null) {
			return super.getJdbcTimeZone();
		} else {
			return timezone;
		}
	}
}
