/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jadira.jms.container;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.jadira.jms.mdp.AbstractMessageDriven;
import org.springframework.jms.connection.ConnectionFactoryUtils;
import org.springframework.jms.connection.JmsResourceHolder;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.AbstractPollingMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.JmsUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * BatchedMessageListenerContainer is an extension of Spring's {@link DefaultMessageListenerContainer} which batches multiple message reads into each single transaction. Batching message reads
 * typically provides significant enhancement to the throughput of message reads in message queue environments.
 * <p>
 * To use this class you must inject a transaction manager via {@link AbstractPollingMessageListenerContainer#setTransactionManager(org.springframework.transaction.PlatformTransactionManager)}.
 * </p>
 * <p>
 * The class performs a blocking read for the first message read in any transaction. The blocking duration is determined by {@link AbstractPollingMessageListenerContainer#setReceiveTimeout(long)}.
 * Subsequent messages up to the configured {@link #setMaxMessagesPerTransaction(int)} batch limit (which defaults to 150) are performed as non-blocking reads, with the bach completing as soon as the
 * message queue cannot provide further messages.
 * </p>
 * <p>
 * Users of this class must handle rollback appropriately. A rollback triggered by failure processing a single message will cause all the messages in the transaction to rollback. It is recommended to
 * design you message processing so that rollback only occurs for fatal, unexpected and unrecoverable errors such as a failure in the infrastructure. You should handle other errors by, for example,
 * delivering messages directly to an error queue rather than throwing an exception. To assist in constructing this pattern, the {@link AbstractMessageDriven} POJO is also provided which provides the
 * basic framework for implementing a {@link MessageListener} that is aligned with this contract.
 * </p>
 * <p>
 * The class contains an optional feature called RetryMitigation which is enabled by default. RetryMitigation prevents further messages being read in a batch if any message is identified as being one that
 * is redelivered. When retryMitigation is enabled, any failure in processing will also trigger a pessimistic message mode. Once in pessimistic message mode messages are read one message at a time. This mode remains in place
 * until either the number of messages in a batch multiplied by the number of concurrent consumers have been read since the last failure, or the queue cannot provide further messages (i.e. is empty).
 * The aim of this feature is to reduce the likelihood of messages reaching the redelivery limit due to a bad message in the batch.
 * </p>
 * You can also configure the class to conclude any batch when a redelivered message is encountered (again the default behaviour). This feature complements RetryMitigation.
 * <p>
 * NB. Due to the design and structure of Spring's {@link DefaultMessageListenerContainer} and its superclasses, implementing this class must by necessity duplicate certain parts of
 * {@link DefaultMessageListenerContainer}. Consequently, this class has been managed at a source code level as a derivative of {@link DefaultMessageListenerContainer} and copyright messages and
 * attributions reflect this.
 * </p>
 * @author Juergen Hoeller was the original author of the {@link DefaultMessageListenerContainer}. The class was modified, extended and renamed to enable batching by Chris Pheby.
 */
public class BatchedMessageListenerContainer extends DefaultMessageListenerContainer {

    /**
     * Default number of messages to read per transaction
     */
    public static final int DEFAULT_BATCH_SIZE = 150;

    /**
     * Number of messages to read per transaction
     */
    private int maxMessagesPerTransaction = DEFAULT_BATCH_SIZE;
    
    private final MessageListenerContainerResourceFactory transactionalResourceFactory = new MessageListenerContainerResourceFactory();

    private boolean retryMitigation = true;
    
    private boolean concludeBatchOnRedeliveredMessage = true;
    
    private volatile boolean pessimisticMessageMode = false;

    private volatile int pessimisticMessageReads = 0;
    
    /**
     * Create a new instance
     */
    public BatchedMessageListenerContainer() {
    }

    /**
     * Configures the maximum number of messages that can be read in a single transaction
     * @param maxMessagesPerTransaction The requested maximum number of messages per transaction
     */
    public void setMaxMessagesPerTransaction(int maxMessagesPerTransaction) {
        this.maxMessagesPerTransaction = maxMessagesPerTransaction;
    }

    /**
     * Get the configured maximum number of messages per transaction
     * @return The maximum number of messages per transaction
     */
    public int getMaxMessagesPerTransaction() {
        return maxMessagesPerTransaction;
    }

    /**
     * True if the instance attempts to mitigate the problems arising when messages in a batch
     * are all retried when a poison message is encountered
     * @return True if RetryMitigation is enabled
     */
    public boolean isRetryMitigation() {
        return retryMitigation;
    }

    /**
     * Enables or disables the RetryMitigation functionality
     * @param retryMitigation True if RetryMitigation is to be enabled, false otherwise
     */
    public void setRetryMitigation(boolean retryMitigation) {
        this.retryMitigation = retryMitigation;
    }

    /**
     * True if seeing a redelivered message will conclude the current batch
     * @return True if ConcludeBatchOnRedeliveredMessage is enabled
     */
    public boolean isConcludeBatchOnRedeliveredMessage() {
        return concludeBatchOnRedeliveredMessage;
    }

    /**
     * Enables or disables the ConcludeBatchOnRedeliveredMessage functionality
     * @param concludeBatchOnRedeliveredMessage True if ConcludeBatchOnRedeliveredMessage is to be enabled, false otherwise
     */
    public void setConcludeBatchOnRedeliveredMessage(boolean concludeBatchOnRedeliveredMessage) {
        this.concludeBatchOnRedeliveredMessage = concludeBatchOnRedeliveredMessage;
    }

    @Override
    protected boolean doReceiveAndExecute(Object invoker, Session session, MessageConsumer consumer,
            TransactionStatus status) throws JMSException {

        Connection connectionToClose = null;
        Session sessionToClose = null;
                
        MessageConsumer consumerToClose = null;

        final List<Message> messages;
        Message message;
        
        try {           
            boolean transactional = false;

            if (session == null) {

                session = ConnectionFactoryUtils.doGetTransactionalSession(getConnectionFactory(),
                        transactionalResourceFactory, true);
                transactional = (session != null);
            }

            if (session == null) {

                final Connection connection;
                if (sharedConnectionEnabled()) {
                    connection = getSharedConnection();
                } else {
                    connection = createConnection();
                    connectionToClose = connection;
                    connection.start();
                }

                session = createSession(connection);
                sessionToClose = session;
            }

            if (consumer == null) {

                consumer = createListenerConsumer(session);
                consumerToClose = consumer;
            }

            messages = new ArrayList<Message>();

            message = receiveMessage(consumer);
            if (message != null) {

                messages.add(message);
                if (logger.isDebugEnabled()) {
                    logger.debug("Received message of type [" + message.getClass() + "] from consumer [" + consumer
                            + "] of " + (transactional ? "transactional " : "") + "session [" + session + "]");
                }
                if (pessimisticMessageMode) {
                    pessimisticMessageReads = pessimisticMessageReads + 1;
                }
            } else {
                pessimisticMessageMode = false;
            }

            int count = 0;

            // Check the delivery account so we can stop batching when we hit a redelivered message
            final int deliveryCount = (concludeBatchOnRedeliveredMessage && message.propertyExists("JMSXDeliveryCount")) ? message.getIntProperty("JMSXDeliveryCount") : -1;
            while ((message != null) && (++count < maxMessagesPerTransaction) && (!retryMitigation || !pessimisticMessageMode) && (!concludeBatchOnRedeliveredMessage || deliveryCount < 2)) {
                
                message = receiveMessageNoWait(consumer);

                if (message != null) {
                    messages.add(message);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Received message of type [" + message.getClass() + "] from consumer [" + consumer
                                + "] of " + (transactional ? "transactional " : "") + "session [" + session + "]");
                    }
                    if (pessimisticMessageMode) {
                        pessimisticMessageReads = pessimisticMessageReads + 1;
                    }
                } else {
                    pessimisticMessageMode = false;
                }
            }
            
            if (pessimisticMessageMode && (pessimisticMessageReads == (maxMessagesPerTransaction * this.getConcurrentConsumers()))) {
                pessimisticMessageMode = false;
            }

            if (messages.size() > 0) {

                // Only if messages were collected, notify the listener to consume the same.
                boolean exposeResource = (!transactional && isExposeListenerSession() && !TransactionSynchronizationManager
                        .hasResource(getConnectionFactory()));
                if (exposeResource) {
                    TransactionSynchronizationManager.bindResource(getConnectionFactory(), new JmsResourceHolder(
                            session));
                }

                try {
                    doExecuteListener(session, messages);
                } catch (Throwable ex) {

                    if (status != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Rolling back transaction because of listener exception thrown: " + ex);
                        }
                        status.setRollbackOnly();
                    }

                    handleListenerException(ex);

                    if (ex instanceof JMSException) {
                        throw (JMSException) ex;
                    }

                } finally {

                    if (exposeResource) {
                        TransactionSynchronizationManager.unbindResource(getConnectionFactory());
                    }

                }
                return true;
            } else {
                if (logger.isTraceEnabled()) {
                    logger.trace("Consumer [" + consumer + "] of " + (transactional ? "transactional " : "")
                            + "session [" + session + "] did not receive a message");
                }
                noMessageReceived(invoker, session);

                // Nevertheless call commit, in order to reset the transaction timeout (if any).
                // However, don't do this on Tibco since this may lead to a deadlock there.
                if (shouldCommitAfterNoMessageReceived(session)) {
                    commitIfNecessary(session, message);
                }
                // Indicate that no message has been received.
                return false;
            }
        } catch (JMSException e) {
            // We record that we last saw an exception - that ensures that only single messages will
            // be read until we hit the redelivered messages
            if (retryMitigation) {
                this.pessimisticMessageMode = true;
                pessimisticMessageReads = 0;
            }
            throw e;
        } catch (RuntimeException e) {
            // We record that we last saw an exception - that ensures that only single messages will
            // be read until we hit the redelivered messages
            if (retryMitigation) {
                this.pessimisticMessageMode = true;
                pessimisticMessageReads = 0;
            }
            throw e;
        } finally {
            JmsUtils.closeMessageConsumer(consumerToClose);
            JmsUtils.closeSession(sessionToClose);
            ConnectionFactoryUtils.releaseConnection(connectionToClose, getConnectionFactory(), true);
        }
    }

    /**
     * A batched variant of {@link DefaultMessageListenerContainer#doExecuteListener(Session, Message)}.
     * 
     * @param session The session
     * @param messages A list of messages
     * @throws JMSException Indicates a problem during processing
     */
    protected void doExecuteListener(Session session, List<Message> messages) throws JMSException {
        if (!isAcceptMessagesWhileStopping() && !isRunning()) {
            if (logger.isWarnEnabled()) {
                logger.warn("Rejecting received messages because of the listener container "
                        + "having been stopped in the meantime: " + messages);
            }
            rollbackIfNecessary(session);
            throw new MessageRejectedWhileStoppingException();
        }

        try {
            for (Message message : messages) {
                invokeListener(session, message);
            }
        } catch (JMSException ex) {
            rollbackOnExceptionIfNecessary(session, ex);
            throw ex;
        } catch (RuntimeException ex) {
            rollbackOnExceptionIfNecessary(session, ex);
            throw ex;
        } catch (Error err) {
            rollbackOnExceptionIfNecessary(session, err);
            throw err;
        }
        commitIfNecessary(session, messages);
    }

    /**
     * Variant of {@link AbstractMessageListenerContainer#commitIfNecessary(Session, Message)} that performs the activity for a batch of messages.
     * @param session the JMS Session to commit
     * @param messages the messages to acknowledge
     * @throws javax.jms.JMSException in case of commit failure
     */
    protected void commitIfNecessary(Session session, List<Message> messages) throws JMSException {
        // Commit session or acknowledge message.
        if (session.getTransacted()) {
            // Commit necessary - but avoid commit call within a JTA transaction.
            if (isSessionLocallyTransacted(session)) {
                // Transacted session created by this container -> commit.
                JmsUtils.commitIfNecessary(session);
            }
        } else if (messages != null && isClientAcknowledge(session)) {
            for (Message message : messages) {
                message.acknowledge();
            }
        }
    }

    @Override
    protected void validateConfiguration() {
        if (maxMessagesPerTransaction < 1) {
            throw new IllegalArgumentException("maxMessagesPerTransaction property must have a value of at least 1");
        }
    }

    /**
     * This is the {@link BatchedMessageListenerContainer}'s equivalent to {@link AbstractPollingMessageListenerContainer#receiveMessage}. Does not block if no message is available.
     * @param consumer The MessageConsumer to use
     * @return The Message, if any
     * @throws JMSException Indicates a problem occurred
     */
    protected Message receiveMessageNoWait(MessageConsumer consumer) throws JMSException {
        return consumer.receiveNoWait();
    }

    /**
     * Internal exception class that indicates a rejected message on shutdown. Used to trigger a rollback for an external transaction manager in that case.
     */
    private static class MessageRejectedWhileStoppingException extends RuntimeException {
        private static final long serialVersionUID = -318011666513960841L;
    }

    /**
     * ResourceFactory implementation that delegates to this listener container's protected callback methods.
     */
    private class MessageListenerContainerResourceFactory implements ConnectionFactoryUtils.ResourceFactory {

        public Connection getConnection(JmsResourceHolder holder) {
            return BatchedMessageListenerContainer.this.getConnection(holder);
        }

        public Session getSession(JmsResourceHolder holder) {
            return BatchedMessageListenerContainer.this.getSession(holder);
        }

        public Connection createConnection() throws JMSException {
            if (BatchedMessageListenerContainer.this.sharedConnectionEnabled()) {
                Connection sharedCon = BatchedMessageListenerContainer.this.getSharedConnection();
                return new SingleConnectionFactory(sharedCon).createConnection();
            } else {
                return BatchedMessageListenerContainer.this.createConnection();
            }
        }

        public Session createSession(Connection con) throws JMSException {
            return BatchedMessageListenerContainer.this.createSession(con);
        }

        public boolean isSynchedLocalTransactionAllowed() {
            return BatchedMessageListenerContainer.this.isSessionTransacted();
        }
    }
}