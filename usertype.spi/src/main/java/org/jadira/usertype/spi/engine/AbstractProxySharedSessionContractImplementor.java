package org.jadira.usertype.spi.engine;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.persistence.FlushModeType;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.ScrollMode;
import org.hibernate.Transaction;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.ExceptionConverter;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.query.Query;
import org.hibernate.query.spi.NativeQueryImplementor;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public abstract class AbstractProxySharedSessionContractImplementor implements SharedSessionContractImplementor {

	private static final long serialVersionUID = -6713694329716655345L;
	
	private SharedSessionContractImplementor target;

	public AbstractProxySharedSessionContractImplementor(SharedSessionContractImplementor target) {
		this.target = target;
	}
	
	protected SharedSessionContractImplementor getTarget() {
		return target;
	}	
	
	@Override
	public void close() throws HibernateException {
		target.close();
	}

	@Override
	public boolean isOpen() {
		return target.isOpen();
	}

	@Override
	public boolean isConnected() {
		return target.isConnected();
	}

	@Override
	public Transaction beginTransaction() {
		return target.beginTransaction();
	}

	@Override
	public Transaction getTransaction() {
		return target.getTransaction();
	}

	@Override
	public ProcedureCall getNamedProcedureCall(String name) {
		return target.getNamedProcedureCall(name);
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName) {
		return target.createStoredProcedureCall(procedureName);
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName, @SuppressWarnings("rawtypes") Class... resultClasses) {
		return target.createStoredProcedureCall(procedureName, resultClasses);
	}

	@Override
	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
		return target.createStoredProcedureCall(procedureName, resultSetMappings);
	}

	@Override
	@Deprecated
	public Criteria createCriteria(@SuppressWarnings("rawtypes") Class persistentClass) {
		return target.createCriteria(persistentClass);
	}

	@Override
	@Deprecated
	public Criteria createCriteria(@SuppressWarnings("rawtypes") Class persistentClass, String alias) {
		return target.createCriteria(persistentClass, alias);
	}

	@Override
	@Deprecated
	public Criteria createCriteria(String entityName) {
		return target.createCriteria(entityName);
	}

	@Override
	@Deprecated
	public Criteria createCriteria(String entityName, String alias) {
		return target.createCriteria(entityName, alias);
	}

	@Override
	public Integer getJdbcBatchSize() {
		return target.getJdbcBatchSize();
	}

	@Override
	public void setJdbcBatchSize(Integer jdbcBatchSize) {
		target.setJdbcBatchSize(jdbcBatchSize);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Query createNamedQuery(String name) {
		return target.createNamedQuery(name);
	}

	@Override
	public JdbcSessionContext getJdbcSessionContext() {
		return target.getJdbcSessionContext();
	}

	@Override
	public JdbcConnectionAccess getJdbcConnectionAccess() {
		return target.getJdbcConnectionAccess();
	}

	@Override
	public TransactionCoordinator getTransactionCoordinator() {
		return target.getTransactionCoordinator();
	}

	@Override
	public void afterTransactionBegin() {
		target.afterTransactionBegin();
	}

	@Override
	public void beforeTransactionCompletion() {
		target.beforeTransactionCompletion();
	}

	@Override
	public void afterTransactionCompletion(boolean successful, boolean delayed) {
		target.afterTransactionCompletion(successful, delayed);
	}

	@Override
	public void flushBeforeTransactionCompletion() {
		target.flushBeforeTransactionCompletion();
	}

	@Override
	public boolean shouldAutoJoinTransaction() {
		return target.shouldAutoJoinTransaction();
	}

	@Override
	public <T> T execute(Callback<T> callback) {
		return target.execute(callback);
	}

	@Override
	public boolean useStreamForLobBinding() {
		return target.useStreamForLobBinding();
	}

	@Override
	public LobCreator getLobCreator() {
		return target.getLobCreator();
	}

	@Override
	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
		return target.remapSqlTypeDescriptor(sqlTypeDescriptor);
	}

	@Override
	public TimeZone getJdbcTimeZone() {
		return target.getJdbcTimeZone();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public QueryImplementor getNamedQuery(String queryName) {
		return target.getNamedQuery(queryName);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public QueryImplementor createQuery(String queryString) {
		return target.createQuery(queryString);
	}

	@Override
	public <R> QueryImplementor<R> createQuery(String queryString, Class<R> resultClass) {
		return target.createQuery(queryString, resultClass);
	}

	@Override
	public <R> QueryImplementor<R> createNamedQuery(String name, Class<R> resultClass) {
		return target.createNamedQuery(name, resultClass);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public NativeQueryImplementor createNativeQuery(String sqlString) {
		return target.createNativeQuery(sqlString);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public NativeQueryImplementor createNativeQuery(String sqlString, Class resultClass) {
		return (NativeQueryImplementor)(target.createNativeQuery(sqlString, resultClass));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public NativeQueryImplementor createNativeQuery(String sqlString, String resultSetMapping) {
		return target.createNativeQuery(sqlString, resultSetMapping);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public NativeQueryImplementor getNamedNativeQuery(String name) {
		return target.getNamedNativeQuery(name);
	}

	@Override
	public SessionFactoryImplementor getFactory() {
		return target.getFactory();
	}

	@Override
	public SessionEventListenerManager getEventListenerManager() {
		return target.getEventListenerManager();
	}

	@Override
	public PersistenceContext getPersistenceContext() {
		return target.getPersistenceContext();
	}

	@Override
	public JdbcCoordinator getJdbcCoordinator() {
		return target.getJdbcCoordinator();
	}

	@Override
	public JdbcServices getJdbcServices() {
		return target.getJdbcServices();
	}

	@Override
	public String getTenantIdentifier() {
		return target.getTenantIdentifier();
	}

	@Override
	public UUID getSessionIdentifier() {
		return target.getSessionIdentifier();
	}

	@Override
	public boolean isClosed() {
		return target.isClosed();
	}

	@Override
	public void checkOpen(boolean markForRollbackIfClosed) {
		target.checkOpen(markForRollbackIfClosed);
	}

	@Override
	public void markForRollbackOnly() {
		target.markForRollbackOnly();
	}

	@Override
	public long getTimestamp() {
		return target.getTimestamp();
	}

	@Override
	public boolean isTransactionInProgress() {
		return target.isTransactionInProgress();
	}

	@Override
	public Transaction accessTransaction() {
		return target.accessTransaction();
	}

	@Override
	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
		return target.generateEntityKey(id, persister);
	}

	@Override
	public Interceptor getInterceptor() {
		return target.getInterceptor();
	}

	@Override
	public void setAutoClear(boolean enabled) {
		target.setAutoClear(enabled);
	}

	@Override
	public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
		target.initializeCollection(collection, writing);
	}

	@Override
	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable)
			throws HibernateException {
		return target.internalLoad(entityName, id, eager, nullable);
	}

	@Override
	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
		return target.immediateLoad(entityName, id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List list(String query, QueryParameters queryParameters) throws HibernateException {
		return target.list(query, queryParameters);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
		return target.iterate(query, queryParameters);
	}

	@Override
	public ScrollableResultsImplementor scroll(String query, QueryParameters queryParameters)
			throws HibernateException {
		return target.scroll(query, queryParameters);
	}

	@Override
	public ScrollableResultsImplementor scroll(Criteria criteria, ScrollMode scrollMode) {
		return target.scroll(criteria, scrollMode);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List list(Criteria criteria) {
		return target.list(criteria);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
			throws HibernateException {
		return target.listFilter(collection, filter, queryParameters);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
			throws HibernateException {
		return target.iterateFilter(collection, filter, queryParameters);
	}

	@Override
	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
		return target.getEntityPersister(entityName, object);
	}

	@Override
	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
		return target.getEntityUsingInterceptor(key);
	}

	@Override
	public Serializable getContextEntityIdentifier(Object object) {
		return target.getContextEntityIdentifier(object);
	}

	@Override
	public String bestGuessEntityName(Object object) {
		return target.bestGuessEntityName(object);
	}

	@Override
	public String guessEntityName(Object entity) throws HibernateException {
		return target.guessEntityName(entity);
	}

	@Override
	public Object instantiate(String entityName, Serializable id) throws HibernateException {
		return target.instantiate(entityName, id);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
		return target.listCustomQuery(customQuery, queryParameters);
	}

	@Override
	public ScrollableResultsImplementor scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
			throws HibernateException {
		return target.scrollCustomQuery(customQuery, queryParameters);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
		return target.list(spec, queryParameters);
	}

	@Override
	public ScrollableResultsImplementor scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) {
		return target.scroll(spec, queryParameters);
	}

	@Override
	public int getDontFlushFromFind() {
		return target.getDontFlushFromFind();
	}

	@Override
	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
		return target.executeUpdate(query, queryParameters);
	}

	@Override
	public int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters)
			throws HibernateException {
		return target.executeNativeUpdate(specification, queryParameters);
	}

	@Override
	public CacheMode getCacheMode() {
		return target.getCacheMode();
	}

	@Override
	public void setCacheMode(CacheMode cm) {
		target.setCacheMode(cm);
	}

	@Override
	@Deprecated
	public void setFlushMode(FlushMode flushMode) {
		target.setFlushMode(flushMode);
	}

	@Override
	public FlushModeType getFlushMode() {
		return target.getFlushMode();
	}

	@Override
	public void setHibernateFlushMode(FlushMode flushMode) {
		target.setHibernateFlushMode(flushMode);
	}

	@Override
	public FlushMode getHibernateFlushMode() {
		return target.getHibernateFlushMode();
	}

	@Override
	public Connection connection() {
		return target.connection();
	}

	@Override
	public void flush() {
		target.flush();
	}

	@Override
	public boolean isEventSource() {
		return target.isEventSource();
	}

	@Override
	public void afterScrollOperation() {
		target.afterScrollOperation();
	}

	@Override
	public boolean shouldAutoClose() {
		return target.shouldAutoClose();
	}

	@Override
	public boolean isAutoCloseSessionEnabled() {
		return target.isAutoCloseSessionEnabled();
	}

	@Override
	public LoadQueryInfluencers getLoadQueryInfluencers() {
		return target.getLoadQueryInfluencers();
	}

	@Override
	public ExceptionConverter getExceptionConverter() {
		return target.getExceptionConverter();
	}
}
