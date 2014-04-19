package org.jadira.usertype.spi.jta;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * This class provides an extension of Spring's {@link LocalSessionFactoryBean} which provides a mechanism for associating a PlatformTransactionManager with the Hibernate session.
 * 
 * To use this class set the Hibernate / JTA property 'hibernate.transaction.jta.platform' to org.jadira.usertype.spi.jta.LocalTransactionManagerPlatform. Then configure the chosen transaction manager
 * via the {@link #setTransactionManager(JtaTransactionManager)} method.
 * 
 * If you are using WebSphere be sure to use the {@link SpringWebSphereUowTransactionManager} as the standard Spring provided implementation will not work with this class.
 */
public class HibernateSessionFactoryBean extends LocalSessionFactoryBean {

    private JtaTransactionManager transactionManager;

    private static final ThreadLocal<JtaTransactionManager> configurationTransactionManagerHolder = new ThreadLocal<JtaTransactionManager>();

    static JtaTransactionManager getConfigurationTransactionManager() {
        return configurationTransactionManagerHolder.get();
    }

    @Override
    protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {

        if (this.transactionManager != null) {
            configurationTransactionManagerHolder.set(this.transactionManager);
        }

        try {
            return super.buildSessionFactory(sfb);
        } finally {

            if (this.transactionManager != null) {
                configurationTransactionManagerHolder.set(null);
            }
        }
    }

    /**
     * Associate a transaction manager with this session
     * @param transactionManager The {@link JtaTransactionManager}
     */
    public void setTransactionManager(JtaTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
