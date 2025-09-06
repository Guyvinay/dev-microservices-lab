package com.dev.service;

import com.dev.rmq.annotation.RabbitListener;
import com.dev.rmq.utility.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

//@RabbitListener(value = "RmqListener1", queue = Queues.QUEUE1)
public class RmqListener1 implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RmqListener1.class);

    @Override
    public void onMessage(Message message) {
        log.info("Receiving message from QUEUE2");
        log.info("message {}", message);
    }
}
