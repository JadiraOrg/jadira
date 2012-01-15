package org.jadira.usertype.dateandtime.legacyjdk.descriptor.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.TimeZone;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.DateTypeDescriptor;

/**
 * Descriptor for {@link Types#DATE DATE} handling.
 */
public class LocationSafeDateTypeDescriptor extends DateTypeDescriptor {

	private static final long serialVersionUID = 2320271428720185601L;

	public static final DateTypeDescriptor INSTANCE = new LocationSafeDateTypeDescriptor();

	@Override
	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
		
		return new BasicBinder<X>(javaTypeDescriptor, this) {
			
			@Override
			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
				st.setDate(index, javaTypeDescriptor.unwrap(value, Date.class, options), getUtcCalendar());
			}
		};
	}
	
	@Override
	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
		
		return new BasicExtractor<X>(javaTypeDescriptor, this) {
			
			@Override
			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
				return javaTypeDescriptor.wrap(rs.getDate(name, getUtcCalendar()), options);
			}
		};
	}
	
    protected final Calendar getUtcCalendar() {

        final Calendar utcCalendar = Calendar.getInstance();
        utcCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcCalendar;
    }
}
