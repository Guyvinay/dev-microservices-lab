package com.pros.service;

import com.pros.annotation.QueueListener;
import com.pros.utils.QueueListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

//@QueueListener(value = "RmqListener1", queue = QueueListeners.QUEUE2)
public class RmqListener1 implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RmqListener1.class);

    @Override
    public void onMessage(Message message) {
        log.info("Receiving message from QUEUE2");
        log.info("message {}", message);
    }
}
