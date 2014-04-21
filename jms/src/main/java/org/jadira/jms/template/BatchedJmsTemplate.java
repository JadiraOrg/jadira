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
package org.jadira.jms.template;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.jadira.jms.container.BatchedMessageListenerContainer;
import org.jadira.jms.mdp.AbstractMessageDriven;
import org.springframework.jms.JmsException;
import org.springframework.jms.connection.JmsResourceHolder;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.JmsUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * BatchedJmsTemplate customises Spring's {@link JmsTemplate} with additional methods that enable multiple items to be processed in a single transaction for the various supported operations. The
 * additional methods are identified by the suffix 'batch'.
 * <p>
 * As with {@link BatchedMessageListenerContainer}, Within each transaction, the first read is a blocking read, that blocks for {@link JmsTemplate#setReceiveTimeout(long)}. Subsequent messages up to
 * the maximum batch size {@link #setBatchSize(int)} are read as non-blocking reads, with the batch completing as soon as the queue cannot service further messages.
 * </p>
 * <p>
 * Users of this class must handle rollback appropriately. A rollback triggered by failure processing a single message will cause all the messages in the transaction to rollback. It is recommended to
 * design you message processing so that rollback only occurs for fatal, unexpected and unrecoverable errors such as a failure in the infrastructure. You should handle other errors by, for example,
 * delivering messages directly to an error queue rather than throwing an exception. To assist in constructing this pattern, the {@link AbstractMessageDriven} POJO is also provided which provides the
 * basic framework for implementing a {@link MessageListener} that is aligned with this contract.
 * </p>
 * <p>
 * NB. Due to the design and structure of Spring's {@link DefaultMessageListenerContainer} and its superclasses, implementing this class must by necessity duplicate certain parts of
 * {@link DefaultMessageListenerContainer}. Consequently, this class has been managed at a source code level as a derivative of {@link DefaultMessageListenerContainer} and copyright messages and
 * attributions reflect this.
 * </p>
 * @author Mark Pollack and Juergen Hoeller were the original authors of the {@link JmsTemplate}. Modifications to this class to enable batching were made by Chris Pheby.
 */
public class BatchedJmsTemplate extends JmsTemplate {

    private static final TransactionSynchronization BATCH_SYNCHRONIZATION = new BatchTransactionSynchronization();

    /**
     * Flag to indicate the start of a batch
     */
    private static final ThreadLocal<Boolean> IS_START_OF_BATCH = new ThreadLocal<Boolean>() {
        protected Boolean initialValue() {
            return Boolean.TRUE;
        }
    };

    /**
     * The default maximum size for a transactional batch
     */
    public static final int DEFAULT_BATCH_SIZE = 150;

    /**
     * The configured maximum batch size, must be at least 1
     */
    private int batchSize = DEFAULT_BATCH_SIZE;

    /**
     * Creates a new instance
     */
    public BatchedJmsTemplate() {
    }

    /**
     * Configures the maximum number of messages that can be read in a transaction
     * @param batchSize The maximum batch size
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Get the maximum number of messages that can be read in a transaction
     * @return The maximum batch size
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * Receive a batch of up to default batch size for the default destination. Other than batching this method is the same as {@link JmsTemplate#receive()}
     * @return A list of {@link Message}
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveBatch() throws JmsException {

        return receiveBatch(batchSize);
    }

    /**
     * Receive a batch of up to batchSize. Other than batching this method is the same as {@link JmsTemplate#receive()}
     * @return A list of {@link Message}
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveBatch(int batchSize) throws JmsException {

        Destination defaultDestination = getDefaultDestination();

        if (defaultDestination != null) {
            return receiveBatch(defaultDestination, batchSize);
        } else {
            return receiveBatch(getRequiredDefaultDestinationName(), batchSize);
        }
    }

    /**
     * Receive a batch of up to default batch size for given destination. Other than batching this method is the same as {@link JmsTemplate#receive(Destination)}
     * @return A list of {@link Message}
     * @param destination The Destination
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveBatch(Destination destination) throws JmsException {
        return receiveBatch(destination, batchSize);
    }

    /**
     * Receive a batch of up to batchSize for given destination. Other than batching this method is the same as {@link JmsTemplate#receive(Destination)}
     * @return A list of {@link Message}
     * @param destination The Destination
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveBatch(final Destination destination, final int batchSize) throws JmsException {
        return execute(new SessionCallback<List<Message>>() {
            public List<Message> doInJms(Session session) throws JMSException {
                return doBatchReceive(session, destination, null, batchSize);
            }
        }, true);
    }

    /**
     * Receive a batch of up to default batch size for given destinationName. Other than batching this method is the same as {@link JmsTemplate#receive(String)}
     * @return A list of {@link Message}
     * @param destinationName The Destination name
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveBatch(String destinationName) throws JmsException {
        return receiveBatch(destinationName, getBatchSize());
    }

    /**
     * Receive a batch of up to default batch size for given destination. Other than batching this method is the same as {@link JmsTemplate#receive(String)}
     * @return A list of {@link Message}
     * @param destinationName The Destination
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveBatch(final String destinationName, final int batchSize) throws JmsException {
        return execute(new SessionCallback<List<Message>>() {
            public List<Message> doInJms(Session session) throws JMSException {
                Destination destination = resolveDestinationName(session, destinationName);
                return doBatchReceive(session, destination, null, batchSize);
            }
        }, true);
    }

    /**
     * Receive a batch of up to default batch size for default destination and given message selector. Other than batching this method is the same as {@link JmsTemplate#receiveSelected(String)}
     * @return A list of {@link Message}
     * @param messageSelector The Selector
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveSelectedBatch(String messageSelector) throws JmsException {
        return receiveSelectedBatch(messageSelector, getBatchSize());
    }

    /**
     * Receive a batch of up to batchSize for default destination and given message selector. Other than batching this method is the same as {@link JmsTemplate#receiveSelected(String)}
     * @return A list of {@link Message}
     * @param messageSelector The Selector
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveSelectedBatch(String messageSelector, int batchSize) throws JmsException {
        Destination defaultDestination = getDefaultDestination();
        if (defaultDestination != null) {
            return receiveSelectedBatch(defaultDestination, messageSelector, batchSize);
        } else {
            return receiveSelectedBatch(getRequiredDefaultDestinationName(), messageSelector, batchSize);
        }
    }

    /**
     * Receive a batch of up to default batch size for given destination and message selector. Other than batching this method is the same as {@link JmsTemplate#receiveSelected(Destination, String)}
     * @return A list of {@link Message}
     * @param destination The Destination
     * @param messageSelector The Selector
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveSelectedBatch(Destination destination, String messageSelector) throws JmsException {
        return receiveSelectedBatch(destination, messageSelector, getBatchSize());
    }

    /**
     * Receive a batch of up to batchSize for given destination and message selector. Other than batching this method is the same as {@link JmsTemplate#receiveSelected(Destination, String)}
     * @return A list of {@link Message}
     * @param destination The Destination
     * @param messageSelector The Selector
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveSelectedBatch(final Destination destination, final String messageSelector,
            final int batchSize) throws JmsException {
        return execute(new SessionCallback<List<Message>>() {
            public List<Message> doInJms(Session session) throws JMSException {

                return doBatchReceive(session, destination, messageSelector, batchSize);
            }
        }, true);
    }

    /**
     * Receive a batch of up to default batch size for given destination name and message selector. Other than batching this method is the same as {@link JmsTemplate#receiveSelected(String, String)}
     * @return A list of {@link Message}
     * @param destinationName The destination name
     * @param messageSelector The Selector
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveSelectedBatch(String destinationName, String messageSelector) throws JmsException {
        return receiveSelectedBatch(destinationName, messageSelector, getBatchSize());
    }

    /**
     * Receive a batch of up to batchSize for given destination name and message selector. Other than batching this method is the same as {@link JmsTemplate#receiveSelected(String, String)}
     * @return A list of {@link Message}
     * @param destinationName The destination name
     * @param messageSelector The Selector
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Message> receiveSelectedBatch(final String destinationName, final String messageSelector,
            final int batchSize) throws JmsException {
        return execute(new SessionCallback<List<Message>>() {
            public List<Message> doInJms(Session session) throws JMSException {
                Destination destination = resolveDestinationName(session, destinationName);
                return doBatchReceive(session, destination, messageSelector, batchSize);
            }
        }, true);
    }

    /**
     * Receive a batch of up to default batch size for default destination and convert each message in the batch. Other than batching this method is the same as {@link JmsTemplate#receiveAndConvert()}
     * @return A list of {@link Message}
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveAndConvertBatch() throws JmsException {
        return receiveAndConvertBatch(getBatchSize());
    }

    /**
     * Receive a batch of up to batchSize for default destination and convert each message in the batch. Other than batching this method is the same as {@link JmsTemplate#receiveAndConvert()}
     * @return A list of {@link Message}
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveAndConvertBatch(int batchSize) throws JmsException {
        List<Message> messages = receiveBatch(batchSize);
        List<Object> result = new ArrayList<Object>(messages.size());
        for (Message next : messages) {
            result.add(doConvertFromMessage(next));
        }
        return result;
    }

    /**
     * Receive a batch of up to default batch size for the given Destination and convert each message in the batch. Other than batching this method is the same as
     * {@link JmsTemplate#receiveAndConvert(Destination)}
     * @return A list of {@link Message}
     * @param destination The Destination
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveAndConvertBatch(Destination destination) throws JmsException {
        return receiveAndConvertBatch(destination, getBatchSize());
    }

    /**
     * Receive a batch of up to batchSize for given Destination and convert each message in the batch. Other than batching this method is the same as {@link JmsTemplate#receiveAndConvert(Destination)}
     * @return A list of {@link Message}
     * @param destination The Destination
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveAndConvertBatch(Destination destination, int batchSize) throws JmsException {
        List<Message> messages = receiveBatch(destination, batchSize);
        List<Object> result = new ArrayList<Object>(messages.size());
        for (Message next : messages) {
            result.add(doConvertFromMessage(next));
        }
        return result;
    }

    /**
     * Receive a batch of up to default batch size for given destination name and convert each message in the batch. Other than batching this method is the same as
     * {@link JmsTemplate#receiveAndConvert(String)}
     * @return A list of {@link Message}
     * @param destinationName The destination name
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveAndConvertBatch(String destinationName) throws JmsException {
        return receiveAndConvertBatch(destinationName, getBatchSize());
    }

    /**
     * Receive a batch of up to batchSize for given destination name and convert each message in the batch. Other than batching this method is the same as {@link JmsTemplate#receiveAndConvert(String)}
     * @return A list of {@link Message}
     * @param destinationName The destination name
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveAndConvertBatch(String destinationName, int batchSize) throws JmsException {
        List<Message> messages = receiveBatch(destinationName, batchSize);
        List<Object> result = new ArrayList<Object>(messages.size());
        for (Message next : messages) {
            result.add(doConvertFromMessage(next));
        }
        return result;
    }

    /**
     * Receive a batch of up to default batch size for default destination and message selector and convert each message in the batch. Other than batching this method is the same as
     * {@link JmsTemplate#receiveSelectedAndConvert(String)}
     * @return A list of {@link Message}
     * @param messageSelector The Selector
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveSelectedAndConvertBatch(String messageSelector) throws JmsException {
        return receiveSelectedAndConvertBatch(messageSelector, getBatchSize());
    }

    /**
     * Receive a batch of up to batchSize for default destination and message selector and convert each message in the batch. Other than batching this method is the same as
     * {@link JmsTemplate#receiveSelectedAndConvert(String)}
     * @return A list of {@link Message}
     * @param messageSelector The Selector
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveSelectedAndConvertBatch(String messageSelector, int batchSize) throws JmsException {
        List<Message> messages = receiveSelectedBatch(messageSelector, batchSize);
        List<Object> result = new ArrayList<Object>(messages.size());
        for (Message next : messages) {
            result.add(doConvertFromMessage(next));
        }
        return result;
    }

    /**
     * Receive a batch of up to default batch size for given Destination and message selector and convert each message in the batch. Other than batching this method is the same as
     * {@link JmsTemplate#receiveSelectedAndConvert(Destination, String)}
     * @return A list of {@link Message}
     * @param destination The Destination
     * @param messageSelector The Selector
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveSelectedAndConvertBatch(Destination destination, String messageSelector)
            throws JmsException {
        return receiveSelectedAndConvertBatch(destination, messageSelector, getBatchSize());
    }

    /**
     * Receive a batch of up to batchSize for given Destination and message selector and convert each message in the batch. Other than batching this method is the same as
     * {@link JmsTemplate#receiveSelectedAndConvert(Destination, String)}
     * @return A list of {@link Message}
     * @param destination The Destination
     * @param messageSelector The Selector
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveSelectedAndConvertBatch(Destination destination, String messageSelector, int batchSize)
            throws JmsException {
        List<Message> messages = receiveSelectedBatch(destination, messageSelector, batchSize);
        List<Object> result = new ArrayList<Object>(messages.size());
        for (Message next : messages) {
            result.add(doConvertFromMessage(next));
        }
        return result;
    }

    /**
     * Receive a batch of up to default batch size for given destination name and message selector and convert each message in the batch. Other than batching this method is the same as
     * {@link JmsTemplate#receiveSelectedAndConvert(String, String)}
     * @return A list of {@link Message}
     * @param destinationName The destination name
     * @param messageSelector The Selector
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveSelectedAndConvertBatch(String destinationName, String messageSelector)
            throws JmsException {
        return receiveSelectedAndConvertBatch(destinationName, messageSelector, getBatchSize());
    }

    /**
     * Receive a batch of up to batchSize for given destination name and message selector and convert each message in the batch. Other than batching this method is the same as
     * {@link JmsTemplate#receiveSelectedAndConvert(String, String)}
     * @return A list of {@link Message}
     * @param destinationName The destination name
     * @param messageSelector The Selector
     * @param batchSize The batch size
     * @throws JmsException The {@link JmsException}
     */
    public List<Object> receiveSelectedAndConvertBatch(String destinationName, String messageSelector, int batchSize)
            throws JmsException {
        List<Message> messages = receiveSelectedBatch(destinationName, batchSize);
        List<Object> result = new ArrayList<Object>(messages.size());
        for (Message next : messages) {
            result.add(doConvertFromMessage(next));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message receive() throws JmsException {
        Destination defaultDestination = getDefaultDestination();
        if (defaultDestination != null) {
            return receive(defaultDestination);
        } else {
            return receive(getRequiredDefaultDestinationName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message receive(final Destination destination) throws JmsException {
        return execute(new SessionCallback<Message>() {
            public Message doInJms(Session session) throws JMSException {
                return doSingleReceive(session, destination, null);
            }
        }, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message receive(final String destinationName) throws JmsException {
        return execute(new SessionCallback<Message>() {
            public Message doInJms(Session session) throws JMSException {
                Destination destination = resolveDestinationName(session, destinationName);
                return doSingleReceive(session, destination, null);
            }
        }, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message receiveSelected(String messageSelector) throws JmsException {
        Destination defaultDestination = getDefaultDestination();
        if (defaultDestination != null) {
            return receiveSelected(defaultDestination, messageSelector);
        } else {
            return receiveSelected(getRequiredDefaultDestinationName(), messageSelector);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message receiveSelected(final Destination destination, final String messageSelector) throws JmsException {
        return execute(new SessionCallback<Message>() {
            public Message doInJms(Session session) throws JMSException {

                return doSingleReceive(session, destination, messageSelector);
            }
        }, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message receiveSelected(final String destinationName, final String messageSelector) throws JmsException {
        return execute(new SessionCallback<Message>() {
            public Message doInJms(Session session) throws JMSException {
                Destination destination = resolveDestinationName(session, destinationName);
                return doSingleReceive(session, destination, messageSelector);
            }
        }, true);
    }

    /**
     * Method replicates the simple logic from JmsTemplate#getRequiredDefaultDestinationName which is private and therefore cannot be accessed from this class
     * @return The default destination name
     * @throws IllegalStateException Example if no destination or destination name is specified
     */
    private String getRequiredDefaultDestinationName() throws IllegalStateException {
        String name = getDefaultDestinationName();
        if (name == null) {
            throw new IllegalStateException(
                    "No 'defaultDestination' or 'defaultDestinationName' specified. Check configuration of JmsTemplate.");
        }
        return name;
    }

    /**
     * Method replicates the simple logic from JmsTemplate#doReceive(MessageConsumer, long) which is private and therefore cannot be accessed from this class
     * @param consumer The consumer to use
     * @param timeout The timeout to apply
     * @return A Message
     * @throws JMSException Indicates an error occurred
     */
    private Message doReceive(MessageConsumer consumer, long timeout) throws JMSException {

        if (timeout == RECEIVE_TIMEOUT_NO_WAIT) {
            return consumer.receiveNoWait();
        } else if (timeout > 0) {
            return consumer.receive(timeout);
        } else {
            return consumer.receive();
        }
    }

    protected List<Message> doBatchReceive(Session session, Destination destination, String messageSelector,
            int batchSize) throws JMSException {
        return doBatchReceive(session, createConsumer(session, destination, messageSelector), batchSize);
    }

    protected List<Message> doBatchReceive(Session session, MessageConsumer consumer, int batchSize)
            throws JMSException {

        try {
            final List<Message> result;
            long timeout = determineTimeout();

            Message message = doReceive(consumer, timeout);

            if (message == null) {
                result = new ArrayList<Message>(0);
            } else {
                result = new ArrayList<Message>(batchSize);
                result.add(message);
                for (int i = 1; i < batchSize; i++) {
                    message = doReceive(consumer, RECEIVE_TIMEOUT_NO_WAIT);
                    if (message == null) {
                        break;
                    }
                    result.add(message);
                }
            }

            if (session.getTransacted()) {
                if (isSessionLocallyTransacted(session)) {
                    JmsUtils.commitIfNecessary(session);
                }
            } else if (isClientAcknowledge(session)) {
                if (message != null) {
                    message.acknowledge();
                }
            }
            return result;
        } finally {
            JmsUtils.closeMessageConsumer(consumer);
        }
    }

    protected Message doSingleReceive(Session session, Destination destination, String messageSelector)
            throws JMSException {
        return doSingleReceive(session, createConsumer(session, destination, messageSelector));
    }

    protected Message doSingleReceive(Session session, MessageConsumer consumer) throws JMSException {

        if (!session.getTransacted() || isSessionLocallyTransacted(session)) {
            // If we are not using JTA we should use standard JmsTemplate behaviour
            return super.doReceive(session, consumer);
        }

        // Otherwise batching - the batch can span multiple receive() calls, until you commit the
        // batch
        try {
            final Message message;
            if (Boolean.TRUE.equals(IS_START_OF_BATCH.get())) {
                // Register Synchronization
                TransactionSynchronizationManager.registerSynchronization(BATCH_SYNCHRONIZATION);

                // Use transaction timeout (if available).
                long timeout = determineTimeout();

                message = doReceive(consumer, timeout);
                IS_START_OF_BATCH.set(Boolean.FALSE);
            } else {
                message = doReceive(consumer, RECEIVE_TIMEOUT_NO_WAIT);
            }

            if (isClientAcknowledge(session)) {
                // Manually acknowledge message, if any.
                if (message != null) {
                    message.acknowledge();
                }
            }
            return message;
        } finally {
            JmsUtils.closeMessageConsumer(consumer);
        }
    }

/**
	 * Determines receive timeout, using logic equivalent to that of {@link JmsTemplate#doReceive(Session, MessageConsumer) 
	 * @return The timeout determined
	 */
    private long determineTimeout() {

        long timeout = getReceiveTimeout();

        JmsResourceHolder resourceHolder = (JmsResourceHolder) TransactionSynchronizationManager
                .getResource(getConnectionFactory());
        if (resourceHolder != null && resourceHolder.hasTimeout()) {
            timeout = Math.min(timeout, resourceHolder.getTimeToLiveInMillis());
        }
        return timeout;
    }

    /**
     * A simple TransactionSynchronization implementation that resets the batch indicator so that the next read begins a new batch
     */
    private static class BatchTransactionSynchronization extends TransactionSynchronizationAdapter {

        @Override
        public void afterCompletion(int status) {
            IS_START_OF_BATCH.set(Boolean.TRUE);
        }
    }
}