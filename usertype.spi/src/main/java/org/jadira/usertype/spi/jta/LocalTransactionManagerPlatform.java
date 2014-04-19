/*
 *  Copyright 2013 Chris Pheby
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
package org.jadira.usertype.spi.jta;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * This is a generally useful implementation of Hibernate's JtaPlatform which delegates to a configurable Spring PlatformTransactionManager instance.
 * 
 * To use this class set the Hibernate / JTA property 'hibernate.transaction.jta.platform' to org.jadira.usertype.spi.jta.LocalTransactionManagerPlatform.
 * 
 * In addition this class must be used in conjunction with either {@link HibernateSessionFactoryBean} or {@link HibernateEntityManagerFactoryBean}. These are specialised subclasses of
 * {@link LocalSessionFactoryBean} and {@link LocalContainerEntityManagerFactoryBean} respectively. In both cases, they can be configured with a PlatformTransactionManager (via setTransactionManager()
 * on both the mentioned classes). This configured transaction manager is then automatically made available to this class during initialisation.
 * 
 * It possible (although unusual) to configure different Spring transaction managers for different EntityManagers and Sessions within the same application when using this class.
 */
public class LocalTransactionManagerPlatform extends AbstractJtaPlatform implements JtaPlatform {

    private static final long serialVersionUID = 8676743510117311360L;

    private volatile JtaTransactionManager transactionManager;

    public LocalTransactionManagerPlatform() {

        JtaTransactionManager tm = HibernateEntityManagerFactoryBean.getConfigurationTransactionManager();
        if (tm == null) {
            tm = HibernateSessionFactoryBean.getConfigurationTransactionManager();
        }

        if (tm == null) {
            throw new IllegalStateException(
                    "No JTA TransactionManager found - "
                            + "'hibernate.transaction.jta.platform' property must be set on the containing LocalSessionFactoryBean or LocalEntityManagerFactoryBean as appropriate");
        }

        this.transactionManager = tm;
    }

    @Override
    protected TransactionManager locateTransactionManager() {
        return transactionManager.getTransactionManager();
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return transactionManager.getUserTransaction();
    }
}
