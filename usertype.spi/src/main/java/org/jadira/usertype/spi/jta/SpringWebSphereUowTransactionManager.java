package org.jadira.usertype.spi.jta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.omg.CORBA.SystemException;
import org.springframework.jndi.JndiTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.jta.WebSphereUowTransactionManager;

/**
 * Spring provides an implementation of {@link PlatformTransactionManager} which works properly with WebSphere's Unit of Work (UoW) API. Unfortunately, this implementation has problems when you need
 * to work with the underlying transaction manager, as the UoW API is providing its own transaction management abstraction distinct from JTA.
 * <p>
 * This class subclasses Spring's implementation and provides an adapter that bridges the JTA TransactionManager API to the UoW API.
 * </p>
 * <p>
 * You should use this implementation, for example, when using the {@link LocalTransactionManagerPlatform} with Hibernate and WebSphere. It is compatible with WebSphere 6.1.0.9 and above.
 * </p>
 * For further documentation and configuration options refer to {@link WebSphereUowTransactionManager}.
 */
public class SpringWebSphereUowTransactionManager extends WebSphereUowTransactionManager {

    private static final long serialVersionUID = 4838070722625854290L;

    private static final String UOW_SYNCHRONIZATION_REGISTRY_JNDINAME = "java:comp/websphere/UOWSynchronizationRegistry";
    private static final String USER_TRANASCTION_JNDINAME = "java:comp/UserTransaction";

    private static final Field UOW_FIELD;

    static {
        try {
            UOW_FIELD = WebSphereUowTransactionManager.class.getDeclaredField("uowManager");
            UOW_FIELD.setAccessible(true);
        } catch (SecurityException e) {
            throw new IllegalStateException(
                    "Not permitted to access WebSphereUowTransactionManager: " + e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Could not find WebSphereUowTransactionManager: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new instance
     */
    public SpringWebSphereUowTransactionManager() {
        super();
    }

    @Override
    public void afterPropertiesSet() throws TransactionSystemException {
        super.afterPropertiesSet();
        setTransactionManager(new TransactionManagerAdapter(getJndiTemplate(), retrieveUowManager()));
        setUserTransactionName(USER_TRANASCTION_JNDINAME);
    }

    private Object retrieveUowManager() {
        try {
            Object uowManager = UOW_FIELD.get(this);
            return uowManager;
        } catch (SecurityException e) {
            throw new IllegalStateException(
                    "Not permitted to access WebSphereUowTransactionManager: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unexpected argument accessing WebSphereUowTransactionManager: "
                    + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unexpected exception accessing WebSphereUowTransactionManager: "
                    + e.getMessage(), e);
        }
    }

    /**
     * An adapter that fulfils the JTA {@link TransactionManager} by delegating to the WebSphereUOWTransactionManager
     */
    public static class TransactionManagerAdapter implements TransactionManager {

        private final JndiTemplate jndiTemplate;

        private final Object uowManager;
        private final Class<?> uowManagerClass;

        private final Object uowSynchronizationRegistry;
        private final Class<?> uowSynchronizationRegistryClass;

        private final Method registerSynchronizationMethod;
        private final Method setRollbackOnlyMethod;

        private final Class<?> extendedJTATransactionClass;
        private final Method getLocalIdMethod;

        /**
         * Create a new instance
         * @param jndiTemplate An instance of Spring's JndiTemplate to use to look up resources
         * @param uowManager UOWManager to use
         */
        private TransactionManagerAdapter(JndiTemplate jndiTemplate, Object uowManager) {

            try {
                this.uowManagerClass = Class.forName("com.ibm.ws.uow.UOWManager");

                this.uowSynchronizationRegistry = jndiTemplate.lookup(UOW_SYNCHRONIZATION_REGISTRY_JNDINAME);
                this.uowSynchronizationRegistryClass = Class
                        .forName("com.ibm.websphere.uow.UOWSynchronizationRegistry");

                this.registerSynchronizationMethod = uowSynchronizationRegistryClass.getMethod(
                        "registerInterposedSynchronization", new Class[] { Synchronization.class });
                this.setRollbackOnlyMethod = uowManagerClass.getMethod("setRollbackOnly", new Class[] {});

                this.extendedJTATransactionClass = Class
                        .forName("com.ibm.websphere.jtaextensions.ExtendedJTATransaction");
                this.getLocalIdMethod = extendedJTATransactionClass.getMethod("getLocalId", (Class[]) null);

            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Could not find required WebSphere class: " + e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Could not find required method: " + e.getMessage(), e);
            } catch (NamingException e) {
                throw new IllegalStateException("Problem accessing JNDI: " + e.getMessage(), e);
            }

            this.jndiTemplate = jndiTemplate;
            this.uowManager = uowManager;
        }

        @Override
        public void begin() {
            throw new UnsupportedOperationException("begin() is not supported");
        }

        @Override
        public void commit() {
            throw new UnsupportedOperationException("commit() is not supported");
        }

        @Override
        public int getStatus() {
            throw new UnsupportedOperationException("getStatus() is not supported");
        }

        @Override
        public void resume(Transaction txn) {
            throw new UnsupportedOperationException("resume() is not supported");
        }

        @Override
        public void rollback() {
            throw new UnsupportedOperationException("rollback() is not supported");
        }

        @Override
        public void setTransactionTimeout(int i) {
            throw new UnsupportedOperationException("setTransactionTimeout() is not supported");
        }

        @Override
        public Transaction suspend() {
            throw new UnsupportedOperationException("suspend() is not supported");
        }

        @Override
        public void setRollbackOnly() throws IllegalStateException {
            try {
                setRollbackOnlyMethod.invoke(uowManager, new Object[] {});
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not access setRollbackOnly() on UOWManager: " + e.getMessage(),
                        e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Could not invoke setRollbackOnly() on UOWManager: " + e.getMessage(),
                        e);
            }
        }

        @Override
        public Transaction getTransaction() {
            return new TransactionAdapter(jndiTemplate);
        }

        /**
         * An adapter that fulfils the JTA transaction interface.
         */
        public class TransactionAdapter implements Transaction {

            private final Object extendedJTATransaction;

            /**
             * Creates a new instance
             * @param template The JndiTemplate
             */
            private TransactionAdapter(JndiTemplate template) {
                try {
                    extendedJTATransaction = template.lookup("java:comp/websphere/ExtendedJTATransaction");

                } catch (NamingException e) {
                    throw new IllegalStateException("Could not find ExtendedJTATransaction in JNDI: " + e.getMessage(),
                            e);
                }
            }

            @Override
            public void registerSynchronization(final Synchronization synchronization) {

                try {
                    registerSynchronizationMethod.invoke(uowSynchronizationRegistry, new Object[] { synchronization });
                } catch (IllegalArgumentException e) {
                    throw new IllegalStateException("Unexpected argument accessing UOWSynchronizationRegistry: "
                            + e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Unexpected exception accessing UOWSynchronizationRegistry: "
                            + e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException(
                            "Could not invoke registerSynchronization() on UOWSynchronizationRegistry: "
                                    + e.getMessage(), e);
                }
            }

            @Override
            public void commit() {
                throw new UnsupportedOperationException("commit() is not supported");
            }

            @Override
            public boolean delistResource(XAResource resource, int i) {
                throw new UnsupportedOperationException("delistResource() is not supported");
            }

            @Override
            public boolean enlistResource(XAResource resource) {
                throw new UnsupportedOperationException("enlistResource() is not supported");
            }

            @Override
            public int getStatus() {
                if (0 == getLocalId()) {
                    return Status.STATUS_NO_TRANSACTION;
                } else {
                    return Status.STATUS_ACTIVE;
                }
            }

            @Override
            public void rollback() throws IllegalStateException, SystemException {
                throw new UnsupportedOperationException("rollback() is not supported");
            }

            @Override
            public void setRollbackOnly() {
                try {
                    setRollbackOnlyMethod.invoke(uowManager, new Object[] {});
                } catch (IllegalArgumentException e) {
                    throw new IllegalStateException("Unexpected argument accessing UOWManager: " + e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Unexpected exception accessing UOWManager: " + e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Could not invoke setRollbackOnly() on UOWManager: "
                            + e.getMessage(), e);
                }
            }

            @Override
            public int hashCode() {
                return getLocalId();
            }

            @Override
            public boolean equals(Object other) {
                if (!(other instanceof TransactionAdapter))
                    return false;
                TransactionAdapter that = (TransactionAdapter) other;
                return getLocalId() == that.getLocalId();
            }

            private int getLocalId() {
                try {
                    return ((Integer) (getLocalIdMethod.invoke(extendedJTATransaction, (Object[]) null))).intValue();
                } catch (IllegalArgumentException e) {
                    throw new IllegalStateException("Unexpected argument accessing ExtendedJTATransaction: "
                            + e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Unexpected exception accessing ExtendedJTATransaction: "
                            + e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Could not invoke getLocalId() on ExtendedJTATransaction: "
                            + e.getMessage(), e);
                }
            }

        }
    }
}
