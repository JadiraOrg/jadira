/*
 *  Copyright 2010, 2013, 2014 Chris Pheby
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
package org.jadira.jms.mdp;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.springframework.jms.core.MessageCreator;

/**
 * A MessageCreator that takes a given Message and related Exeption. This class copies the source message and enriches the copy with information about the original headers and properties as well as
 * the causing exception Priority and CorrelationID are preserved on the error message.
 */
public class FatalJmsExceptionMessageCreator implements MessageCreator {

    private static final String EXCEPTION_MESSAGE_PROPERTY = "cause_exceptionMessage";
    private static final String EXCEPTION_STACKTRACE_PROPERTY = "cause_exceptionStackTrace";

    private static final String ORIGINAL_DELIVERY_MODE = "original_JMSDeliveryMode";
    private static final String ORIGINAL_EXPIRATION = "original_JMSExpiration";
    private static final String ORIGINAL_MESSAGE_ID = "original_JMSMessageID";
    private static final String ORIGINAL_REPLY_TO = "original_JMSReplyTo";
    private static final String ORIGINAL_REDELIVERED = "original_JMSRedelivered";
    private static final String ORIGINAL_CORRELATIONID = "original_JMSCorrelationID";
    private static final String ORIGINAL_PRIORITY = "original_JMSPriority";

    private static final int BUFFER_CAPACITY_BYTES = 4096;

    private final Message message;

    private final Exception e;

    public FatalJmsExceptionMessageCreator(Message message, Exception e) {
        this.message = message;
        this.e = e;
    }

    @Override
    public Message createMessage(Session session) throws JMSException {

        final Message copyMessage = copyMessage(session, message);
        enrichMessage(copyMessage, message);
        return copyMessage;
    }

    protected void enrichMessage(Message copyMessage, Message originalMessage) throws JMSException {

        Map<String, Object> messageProps = buildErrorMessageProperties(originalMessage);

        applyMessageProperties(copyMessage, messageProps);
        applyMessageHeaders(copyMessage, originalMessage);
        applyErrorDetails(copyMessage, e);
    }

    protected Map<String, Object> buildErrorMessageProperties(Message msg) throws JMSException {

        Map<String, Object> properties = new HashMap<String, Object>();

        @SuppressWarnings("unchecked")
        Enumeration<String> srcProperties = msg.getPropertyNames();

        while (srcProperties.hasMoreElements()) {
            String propertyName = srcProperties.nextElement();
            properties.put("original_" + propertyName, msg.getObjectProperty(propertyName));
        }

        properties.put(ORIGINAL_DELIVERY_MODE, msg.getJMSDeliveryMode());
        properties.put(ORIGINAL_EXPIRATION, msg.getJMSExpiration());
        properties.put(ORIGINAL_MESSAGE_ID, msg.getJMSMessageID());
        properties.put(ORIGINAL_REPLY_TO, msg.getJMSReplyTo());
        properties.put(ORIGINAL_REDELIVERED, msg.getJMSRedelivered());
        properties.put(ORIGINAL_CORRELATIONID, msg.getJMSCorrelationID());
        properties.put(ORIGINAL_PRIORITY, msg.getJMSPriority());

        return properties;
    }

    private void applyMessageProperties(Message destinationMessage, Map<String, Object> properties) throws JMSException {

        if (properties == null) {
            return;
        }

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            destinationMessage.setObjectProperty(entry.getKey(), entry.getValue());
        }
    }

    protected void applyMessageHeaders(Message destinationMessage, Message sourceMessage) {

        try {
            destinationMessage.setJMSCorrelationIDAsBytes(sourceMessage.getJMSCorrelationIDAsBytes());
        } catch (JMSException e) {
        }
        try {
            destinationMessage.setJMSPriority(sourceMessage.getJMSPriority());
        } catch (JMSException e) {
        }
    }

    protected void applyErrorDetails(final Message destinationMessage, final Exception exception) throws JMSException {

        destinationMessage.setStringProperty(EXCEPTION_MESSAGE_PROPERTY, exception.getMessage());

        StringWriter stackTraceWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTraceWriter));

        destinationMessage.setStringProperty(EXCEPTION_STACKTRACE_PROPERTY, stackTraceWriter.toString());
    }

    private static Message copyMessage(Session session, Message originalMessage) throws JMSException {

        Message copyMessage;

        if (originalMessage instanceof BytesMessage) {

            final BytesMessage theMessage = session.createBytesMessage();
            final byte[] bytes = extractByteArrayFromMessage((BytesMessage) originalMessage);
            theMessage.writeBytes(bytes);

            copyMessage = theMessage;
        } else if (originalMessage instanceof StreamMessage) {

            final StreamMessage theMessage = session.createStreamMessage();
            final byte[] bytes = extractByteArrayFromMessage((StreamMessage) originalMessage);
            theMessage.writeBytes(bytes);

            copyMessage = theMessage;
        } else if (originalMessage instanceof ObjectMessage) {

            copyMessage = session.createObjectMessage(((ObjectMessage) originalMessage).getObject());
        } else if (originalMessage instanceof TextMessage) {

            copyMessage = session.createTextMessage(((TextMessage) originalMessage).getText());
        } else if (originalMessage instanceof MapMessage) {

            MapMessage theMessage = session.createMapMessage();

            @SuppressWarnings("unchecked")
            Enumeration<String> keys = ((MapMessage) originalMessage).getMapNames();
            while (keys.hasMoreElements()) {
                String next = keys.nextElement();
                theMessage.setObject(next, ((MapMessage) originalMessage).getObject(next));
            }

            copyMessage = theMessage;
        } else {
            throw new MessageFormatException("Unexpected Message Type received, was: " + originalMessage.getClass());
        }

        return copyMessage;
    }

    private static byte[] extractByteArrayFromMessage(BytesMessage message) throws JMSException {

        ByteArrayOutputStream oStream = new ByteArrayOutputStream(BUFFER_CAPACITY_BYTES);

        byte[] buffer = new byte[BUFFER_CAPACITY_BYTES];

        int bufferCount = -1;

        while ((bufferCount = message.readBytes(buffer)) >= 0) {
            oStream.write(buffer, 0, bufferCount);
            if (bufferCount < BUFFER_CAPACITY_BYTES) {
                break;
            }
        }

        return oStream.toByteArray();
    }

    private static byte[] extractByteArrayFromMessage(StreamMessage message) throws JMSException {

        ByteArrayOutputStream oStream = new ByteArrayOutputStream(BUFFER_CAPACITY_BYTES);

        byte[] buffer = new byte[BUFFER_CAPACITY_BYTES];

        int bufferCount = -1;

        while ((bufferCount = message.readBytes(buffer)) >= 0) {
            oStream.write(buffer, 0, bufferCount);
            if (bufferCount < BUFFER_CAPACITY_BYTES) {
                break;
            }
        }

        return oStream.toByteArray();
    }
}
