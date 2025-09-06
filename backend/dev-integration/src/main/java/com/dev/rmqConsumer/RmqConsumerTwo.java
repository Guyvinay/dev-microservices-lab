package com.dev.rmqConsumer;

import com.dev.rmq.annotation.RabbitListener;
import com.dev.rmq.utility.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@RabbitListener(value = "RmqConsumerTwo", queue = Queues.QUEUE4)
public class RmqConsumerTwo implements MessageListener {
    Logger log = LoggerFactory.getLogger(RmqConsumerTwo.class);
    @Override
    public void onMessage(Message message) {
        log.info("message received");
        log.info("message {}", message);
    }
}
