/*
 *  Copyright 2010, 2011, 2012 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.usertype.spi.shared;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.dom4j.Node;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.SingleColumnType;
import org.hibernate.type.Type;

public class ColumnMapperSingleColumnTypeAdapter<T,J> implements SingleColumnType<T> {

	private static final long serialVersionUID = -8396126842631394890L;

	private ColumnMapper<T, J> columnMapper;

	public ColumnMapperSingleColumnTypeAdapter(ColumnMapper<T, J> columnMapper) {
		this.columnMapper = columnMapper;
	}
	
	public boolean isAssociationType() {
		return columnMapper.getHibernateType().isAssociationType();
	}

	public boolean isCollectionType() {
		return columnMapper.getHibernateType().isCollectionType();
	}

	public boolean isEntityType() {
		return columnMapper.getHibernateType().isEntityType();
	}

	public boolean isAnyType() {
		return columnMapper.getHibernateType().isAnyType();
	}

	public boolean isComponentType() {
		return columnMapper.getHibernateType().isComponentType();
	}

	public int getColumnSpan(Mapping mapping) throws MappingException {
		return columnMapper.getHibernateType().getColumnSpan(mapping);
	}

	public int[] sqlTypes(Mapping mapping) throws MappingException {
		return new int[] { columnMapper.getSqlType() };
	}

	@SuppressWarnings("rawtypes")
	public Class getReturnedClass() {
		return columnMapper.returnedClass();
	}

	@Deprecated
	public boolean isXMLElement() {
		return columnMapper.getHibernateType().isXMLElement();
	}

	public int getHashCode(Object x, EntityMode mode)
			throws HibernateException {
		return columnMapper.getHibernateType().getHashCode(x, mode);
	}
	
	public int getHashCode(Object x, EntityMode mode, SessionFactoryImplementor sessionFactory)
			throws HibernateException {
		return columnMapper.getHibernateType().getHashCode(x, mode, sessionFactory);
	}
	
	public Object deepCopy(Object x, EntityMode mode, SessionFactoryImplementor sessionFactory)
			throws HibernateException {
		return columnMapper.getHibernateType().deepCopy(x, mode, sessionFactory);
	}

	public boolean isDirty(Object old, Object current,
			SessionImplementor session) throws HibernateException {
		return columnMapper.getHibernateType().isDirty(old, current, session);
	}

	public boolean isDirty(Object oldState, Object currentState,
			boolean[] checkable, SessionImplementor session)
			throws HibernateException {
		return columnMapper.getHibernateType().isDirty(oldState, currentState, checkable, session);
	}

	public boolean isModified(Object dbState, Object currentState,
			boolean[] checkable, SessionImplementor session)
			throws HibernateException {
		return columnMapper.getHibernateType().isModified(dbState, currentState, checkable, session);
	}

	
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {

		@SuppressWarnings("unchecked") final J hibernateValue = (J)(columnMapper.getHibernateType().nullSafeGet(rs, names, session, owner));
		if (hibernateValue == null) {
			return null;
		}
		return columnMapper.fromNonNullValue(hibernateValue);
	}

	public Object nullSafeGet(ResultSet rs, String name,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {

		@SuppressWarnings("unchecked") final J hibernateValue = (J) columnMapper.getHibernateType().nullSafeGet(rs, name, session, owner);
		if (hibernateValue == null) {
			return null;
		}
		return columnMapper.fromNonNullValue(hibernateValue);
	}

	@SuppressWarnings("unchecked")
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			boolean[] settable, SessionImplementor session)
			throws HibernateException, SQLException {

		final Object param = value == null ? null : columnMapper.fromNonNullValue((J) value);
		columnMapper.getHibernateType().nullSafeSet(st, param, index, settable, session);		
	}

	@SuppressWarnings("unchecked")
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		
		final Object param = value == null ? null : columnMapper.toNonNullValue((T) value);		
		columnMapper.getHibernateType().nullSafeSet(st, param, index, session);
	}

	@Deprecated
	public void setToXMLNode(Node node, Object value,
			SessionFactoryImplementor factory) throws HibernateException {
		columnMapper.getHibernateType().setToXMLNode(node, value, factory);
	}

	public String toLoggableString(Object value,
			SessionFactoryImplementor factory) throws HibernateException {
		return columnMapper.getHibernateType().toLoggableString(value, factory);
	}

	@Deprecated
	public Object fromXMLNode(Node xml, Mapping factory)
			throws HibernateException {
		return columnMapper.getHibernateType().fromXMLNode(xml, factory);
	}

	public String getName() {
		return columnMapper.returnedClass().getSimpleName();
	}

	public boolean isMutable() {
		return columnMapper.getHibernateType().isMutable();
	}

	public Serializable disassemble(Object value, SessionImplementor session,
			Object owner) throws HibernateException {
		return columnMapper.getHibernateType().disassemble(value, session, owner);
	}

	public Object assemble(Serializable cached, SessionImplementor session,
			Object owner) throws HibernateException {
		return columnMapper.getHibernateType().assemble(cached, session, owner);
	}

	public void beforeAssemble(Serializable cached, SessionImplementor session) {
		columnMapper.getHibernateType().beforeAssemble(cached, session);
	}

	public Object hydrate(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		return columnMapper.getHibernateType().hydrate(rs, names, session, owner);
	}

	public Object resolve(Object value, SessionImplementor session, Object owner)
			throws HibernateException {
		return columnMapper.getHibernateType().resolve(value, session, owner);
	}

	public Object semiResolve(Object value, SessionImplementor session,
			Object owner) throws HibernateException {
		return columnMapper.getHibernateType().semiResolve(value, session, owner);
	}

	public Type getSemiResolvedType(SessionFactoryImplementor factory) {
		return this;
	}

	public Object replace(Object original, Object target,
			SessionImplementor session, Object owner, @SuppressWarnings("rawtypes") Map copyCache)
			throws HibernateException {
		return columnMapper.getHibernateType().replace(original, target, session, owner, copyCache);
	}

	public Object replace(Object original, Object target,
			SessionImplementor session, Object owner, @SuppressWarnings("rawtypes") Map copyCache,
			ForeignKeyDirection foreignKeyDirection) throws HibernateException {
		return columnMapper.getHibernateType().replace(original, target, session, owner, copyCache, foreignKeyDirection);
	}

	public boolean[] toColumnNullness(Object value, Mapping mapping) {
		return columnMapper.getHibernateType().toColumnNullness(value, mapping);
	}

	public int sqlType() {
		return columnMapper.getSqlType();
	}

	@SuppressWarnings("unchecked")
	public String toString(Object value) throws HibernateException {
		if (value == null) {
			return null;
		}
		return columnMapper.toNonNullString((T) value);
	}

	public T fromStringValue(String xml) throws HibernateException {
		if (xml == null) {
			return null;
		}
		return columnMapper.fromNonNullString(xml);
	}

	public T nullSafeGet(ResultSet rs, String name,
			SessionImplementor session) throws HibernateException, SQLException {

		@SuppressWarnings("unchecked") final J hibernateValue = (J) columnMapper.getHibernateType().nullSafeGet(rs, name, session);
		if (hibernateValue == null) {
			return null;
		}
		return columnMapper.fromNonNullValue(hibernateValue);
	}

	public Object get(ResultSet rs, String name, SessionImplementor session)
			throws HibernateException, SQLException {
		
		@SuppressWarnings("unchecked") final J hibernateValue = (J)(columnMapper.getHibernateType().get(rs, name, session));
		if (hibernateValue == null) {
			return null;
		}
		return columnMapper.fromNonNullValue(hibernateValue);
	}

	@SuppressWarnings("unchecked")
	public void set(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		
		final Object param = value == null ? null : columnMapper.toNonNullValue((T) value);
		columnMapper.getHibernateType().nullSafeSet(st, param, index, session);	}

	public boolean isSame(Object x, Object y, EntityMode entityMode)
			throws HibernateException {
		return columnMapper.getHibernateType().isSame(x, y, entityMode);
	}

	public boolean isEqual(Object x, Object y, EntityMode entityMode)
			throws HibernateException {
		return columnMapper.getHibernateType().isEqual(x, y, entityMode);
	}

	public boolean isEqual(Object x, Object y, EntityMode entityMode,
			SessionFactoryImplementor factory) throws HibernateException {
		return columnMapper.getHibernateType().isEqual(x, y, entityMode, factory);
	}

	public int compare(Object x, Object y, EntityMode entityMode) {
		return columnMapper.getHibernateType().compare(x, y, entityMode);
	}

	public T nullSafeGet(ResultSet rs, String name) throws HibernateException,
			SQLException {

		@SuppressWarnings("unchecked") final J hibernateValue = (J) columnMapper.getHibernateType().nullSafeGet(rs, name, null);
		if (hibernateValue == null) {
			return null;
		}
		return columnMapper.fromNonNullValue(hibernateValue);
	}

	public Object get(ResultSet rs, String name) throws HibernateException,
			SQLException {

		@SuppressWarnings("unchecked") final J hibernateValue = (J)(columnMapper.getHibernateType().get(rs, name, null));
		if (hibernateValue == null) {
			return null;
		}
		return columnMapper.fromNonNullValue(hibernateValue);
	}

	public void nullSafeSet(PreparedStatement st, T value, int index)
			throws HibernateException, SQLException {
		
		@SuppressWarnings("unchecked")
		final Object param = value == null ? null : columnMapper.fromNonNullValue((J) value);
		columnMapper.getHibernateType().nullSafeSet(st, param, index, null);
	}

	public void set(PreparedStatement st, T value, int index)
			throws HibernateException, SQLException {
		
		final Object param = value == null ? null : columnMapper.toNonNullValue((T) value);
		columnMapper.getHibernateType().nullSafeSet(st, param, index, null);	}
	}
