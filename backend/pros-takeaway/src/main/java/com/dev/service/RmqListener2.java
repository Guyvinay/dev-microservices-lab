package com.dev.service;

import com.dev.rmq.annotation.RabbitListener;
import com.dev.rmq.utility.Queues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@RabbitListener(value = "RmqListener2", queue = Queues.QUEUE2)
@Slf4j
public class RmqListener2 implements MessageListener {
    @Override
    public void onMessage(Message message) {
        log.info("messsage received {}", message);
    }
}
