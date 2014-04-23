package org.jadira.usertype.spi.shared.descriptor.jdbc;

import java.sql.Timestamp;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * Based on JdbcTimestampTypeDescriptor
 */
public class JdbcDateTimeTypeDescriptor extends AbstractTypeDescriptor<DateTime> {

    private static final long serialVersionUID = -3432963412566844755L;

    public static final JdbcDateTimeTypeDescriptor INSTANCE = new JdbcDateTimeTypeDescriptor();
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public JdbcDateTimeTypeDescriptor() {
		super( DateTime.class );
	}
	@Override
	public String toString(DateTime value) {
	    
		return new DateTimeFormatterBuilder().appendPattern(TIMESTAMP_FORMAT).toFormatter().print(value);
	}
	@Override
	public DateTime fromString(String string) {
	    return new DateTimeFormatterBuilder().appendPattern(TIMESTAMP_FORMAT).toFormatter().parseDateTime(string);
	}

	@Override
	public boolean areEqual(DateTime one, DateTime another) {
		if ( one == another ) {
			return true;
		}
		if ( one == null || another == null) {
			return false;
		}

		return one.equals(another);
	}

	@Override
	public int extractHashCode(DateTime value) {
		return value.hashCode();
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <X> X unwrap(DateTime value, Class<X> type, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}
		if ( Timestamp.class.isAssignableFrom( type ) ) {
			final Timestamp rtn = new Timestamp(value.getMillis());
			return (X) rtn;
		}
//		if ( java.sql.Date.class.isAssignableFrom( type ) ) {
//			final java.sql.Date rtn = java.sql.Date.class.isInstance( value )
//					? ( java.sql.Date ) value
//					: new java.sql.Date( value.getTime() );
//			return (X) rtn;
//		}
//		if ( java.sql.Time.class.isAssignableFrom( type ) ) {
//			final java.sql.Time rtn = java.sql.Time.class.isInstance( value )
//					? ( java.sql.Time ) value
//					: new java.sql.Time( value.getTime() );
//			return (X) rtn;
//		}
//		if ( Date.class.isAssignableFrom( type ) ) {
//			return (X) value;
//		}
//		if ( Calendar.class.isAssignableFrom( type ) ) {
//			final GregorianCalendar cal = new GregorianCalendar();
//			cal.setTimeInMillis( value.getTime() );
//			return (X) cal;
//		}
//		if ( Long.class.isAssignableFrom( type ) ) {
//			return (X) Long.valueOf( value.getTime() );
//		}
		throw unknownUnwrap( type );
	}
	@Override
	public <X> DateTime wrap(X value, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}
		if ( Timestamp.class.isInstance( value ) ) {
			return new DateTime(((Timestamp) value).getTime());
		}
//
//		if ( Long.class.isInstance( value ) ) {
//			return new Timestamp( (Long) value );
//		}
//
//		if ( Calendar.class.isInstance( value ) ) {
//			return new Timestamp( ( (Calendar) value ).getTimeInMillis() );
//		}
//
//		if ( Date.class.isInstance( value ) ) {
//			return (Date) value;
//		}

		throw unknownWrap( value.getClass() );
	}
}
