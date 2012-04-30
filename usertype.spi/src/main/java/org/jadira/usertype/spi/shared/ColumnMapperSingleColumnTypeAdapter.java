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
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metamodel.relational.Size;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.SingleColumnType;
import org.hibernate.type.Type;

public class ColumnMapperSingleColumnTypeAdapter<T,J> implements SingleColumnType<T> {

	private static final long serialVersionUID = -8396126842631394890L;

	private ColumnMapper<T, J> columnMapper;

	public ColumnMapperSingleColumnTypeAdapter(ColumnMapper<T, J> columnMapper) {
		this.columnMapper = columnMapper;
	}
	
	@Override
	public boolean isAssociationType() {
		return columnMapper.getHibernateType().isAssociationType();
	}

	@Override
	public boolean isCollectionType() {
		return columnMapper.getHibernateType().isCollectionType();
	}

	@Override
	public boolean isEntityType() {
		return columnMapper.getHibernateType().isEntityType();
	}

	@Override
	public boolean isAnyType() {
		return columnMapper.getHibernateType().isAnyType();
	}

	@Override
	public boolean isComponentType() {
		return columnMapper.getHibernateType().isComponentType();
	}

	@Override
	public int getColumnSpan(Mapping mapping) throws MappingException {
		return columnMapper.getHibernateType().getColumnSpan(mapping);
	}

	@Override
	public int[] sqlTypes(Mapping mapping) throws MappingException {
		return new int[] { columnMapper.getSqlType() };
	}

	@Override
	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
		return columnMapper.getHibernateType().dictatedSizes(mapping);
	}

	@Override
	public Size[] defaultSizes(Mapping mapping) throws MappingException {
		return columnMapper.getHibernateType().defaultSizes(mapping);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getReturnedClass() {
		return columnMapper.returnedClass();
	}

	@Override
	public boolean isXMLElement() {
		return columnMapper.getHibernateType().isXMLElement();
	}

	@Override
	public boolean isSame(Object x, Object y) throws HibernateException {
		return columnMapper.getHibernateType().isSame(x, y);
	}

	@Override
	public boolean isEqual(Object x, Object y) throws HibernateException {
		return columnMapper.getHibernateType().isEqual(x, y);
	}

	@Override
	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory)
			throws HibernateException {
		return columnMapper.getHibernateType().isEqual(x, y, factory);
	}

	@Override
	public int getHashCode(Object x) throws HibernateException {
		return columnMapper.getHibernateType().getHashCode(x);
	}

	@Override
	public int getHashCode(Object x, SessionFactoryImplementor factory)
			throws HibernateException {
		return columnMapper.getHibernateType().getHashCode(x, factory);
	}

	@Override
	public int compare(Object x, Object y) {
		return columnMapper.getHibernateType().compare(x, y);
	}

	@Override
	public boolean isDirty(Object old, Object current,
			SessionImplementor session) throws HibernateException {
		return columnMapper.getHibernateType().isDirty(old, current, session);
	}

	@Override
	public boolean isDirty(Object oldState, Object currentState,
			boolean[] checkable, SessionImplementor session)
			throws HibernateException {
		return columnMapper.getHibernateType().isDirty(oldState, currentState, checkable, session);
	}

	@Override
	public boolean isModified(Object dbState, Object currentState,
			boolean[] checkable, SessionImplementor session)
			throws HibernateException {
		return columnMapper.getHibernateType().isModified(dbState, currentState, checkable, session);
	}

	
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {

		@SuppressWarnings("unchecked") final J hibernateValue = (J)(columnMapper.getHibernateType().nullSafeGet(rs, names, session, owner));
		if (hibernateValue == null) {
			return null;
		}
		return columnMapper.fromNonNullValue(hibernateValue);
	}

	@Override
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
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			boolean[] settable, SessionImplementor session)
			throws HibernateException, SQLException {

		if (value != null) {
			value = columnMapper.fromNonNullValue((J) value);
		}
		columnMapper.getHibernateType().nullSafeSet(st, value, index, settable, session);		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		
		if (value != null) {
			value = columnMapper.toNonNullValue((T) value);
		}
		columnMapper.getHibernateType().nullSafeSet(st, value, index, session);
	}

	@Override
	public void setToXMLNode(Node node, Object value,
			SessionFactoryImplementor factory) throws HibernateException {
		columnMapper.getHibernateType().setToXMLNode(node, value, factory);
	}

	@Override
	public String toLoggableString(Object value,
			SessionFactoryImplementor factory) throws HibernateException {
		return columnMapper.getHibernateType().toLoggableString(value, factory);
	}

	@Override
	public Object fromXMLNode(Node xml, Mapping factory)
			throws HibernateException {
		return columnMapper.getHibernateType().fromXMLNode(xml, factory);
	}

	@Override
	public String getName() {
		return columnMapper.returnedClass().getSimpleName();
	}

	@Override
	public Object deepCopy(Object value, SessionFactoryImplementor factory)
			throws HibernateException {
		return columnMapper.getHibernateType().deepCopy(value, factory);
	}

	@Override
	public boolean isMutable() {
		return columnMapper.getHibernateType().isMutable();
	}

	@Override
	public Serializable disassemble(Object value, SessionImplementor session,
			Object owner) throws HibernateException {
		return columnMapper.getHibernateType().disassemble(value, session, owner);
	}

	@Override
	public Object assemble(Serializable cached, SessionImplementor session,
			Object owner) throws HibernateException {
		return columnMapper.getHibernateType().assemble(cached, session, owner);
	}

	@Override
	public void beforeAssemble(Serializable cached, SessionImplementor session) {
		columnMapper.getHibernateType().beforeAssemble(cached, session);
	}

	@Override
	public Object hydrate(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		return columnMapper.getHibernateType().hydrate(rs, names, session, owner);
	}

	@Override
	public Object resolve(Object value, SessionImplementor session, Object owner)
			throws HibernateException {
		return columnMapper.getHibernateType().resolve(value, session, owner);
	}

	@Override
	public Object semiResolve(Object value, SessionImplementor session,
			Object owner) throws HibernateException {
		return columnMapper.getHibernateType().semiResolve(value, session, owner);
	}

	@Override
	public Type getSemiResolvedType(SessionFactoryImplementor factory) {
		return this;
	}

	@Override
	public Object replace(Object original, Object target,
			SessionImplementor session, Object owner, @SuppressWarnings("rawtypes") Map copyCache)
			throws HibernateException {
		return columnMapper.getHibernateType().replace(original, target, session, owner, copyCache);
	}

	@Override
	public Object replace(Object original, Object target,
			SessionImplementor session, Object owner, @SuppressWarnings("rawtypes") Map copyCache,
			ForeignKeyDirection foreignKeyDirection) throws HibernateException {
		return columnMapper.getHibernateType().replace(original, target, session, owner, copyCache, foreignKeyDirection);
	}

	@Override
	public boolean[] toColumnNullness(Object value, Mapping mapping) {
		return columnMapper.getHibernateType().toColumnNullness(value, mapping);
	}

	@Override
	public int sqlType() {
		return columnMapper.getSqlType();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString(Object value) throws HibernateException {
		if (value == null) {
			return null;
		}
		return columnMapper.toNonNullString((T) value);
	}

	@Override
	public T fromStringValue(String xml) throws HibernateException {
		if (xml == null) {
			return null;
		}
		return columnMapper.fromNonNullString(xml);
	}

	@Override
	public T nullSafeGet(ResultSet rs, String name,
			SessionImplementor session) throws HibernateException, SQLException {

		@SuppressWarnings("unchecked") final J hibernateValue = (J) columnMapper.getHibernateType().nullSafeGet(rs, name, session);
		if (hibernateValue == null) {
			return null;
		}
		return columnMapper.fromNonNullValue(hibernateValue);
	}

	@Override
	public Object get(ResultSet rs, String name, SessionImplementor session)
			throws HibernateException, SQLException {
		
		@SuppressWarnings("unchecked") final J hibernateValue = (J)(columnMapper.getHibernateType().get(rs, name, session));
		if (hibernateValue == null) {
			return null;
		}
		return columnMapper.fromNonNullValue(hibernateValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void set(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		
		if (value != null) {
			value = columnMapper.toNonNullValue((T) value);
		}
		columnMapper.getHibernateType().nullSafeSet(st, value, index, session);	}

}
