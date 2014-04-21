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

import javax.jms.Message;
import javax.jms.MessageListener;

import org.jadira.jms.exception.FatalJmsException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;

/**
 * Provides a base implementation for a Message Driven POJO. The key benefit of extending this class is that a standard error handling pattern is 'baked in'. This class should be configured by setting
 * the appropriate JmsTemplate for handling Fatal JMS failures.
 */
public abstract class AbstractMessageDriven implements MessageListener, InitializingBean {

    private JmsTemplate fatalJmsExceptionHandler;

    public JmsTemplate getFatalJmsExceptionHandler() {
        return fatalJmsExceptionHandler;
    }

    public void setFatalJmsExceptionHandler(JmsTemplate fatalJmsExceptionHandler) {
        this.fatalJmsExceptionHandler = fatalJmsExceptionHandler;
    }

    @Override
    public void onMessage(final Message message) {

        if (fatalJmsExceptionHandler == null) {
            doOnMessage(message);
        } else {
            try {
                doOnMessage(message);
            } catch (final FatalJmsException e) {
                fatalJmsExceptionHandler.send(new FatalJmsExceptionMessageCreator(message, e));
            }
        }
    }

    /**
     * Implements message handling functionality
     * @param message Message to be read
     * @throws FatalJmsException Exception thrown to indicate a processing failure that cannot be recovered from
     */
    protected abstract void doOnMessage(Message message) throws FatalJmsException;
}
