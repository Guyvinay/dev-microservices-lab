package com.dev.rmqConsumer;

import com.dev.rmq.annotation.RabbitListener;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@RabbitListener(
        value = "TenantConsumerT",
        queue = "tenant.created.queue",
        exchange = "inndev.tenant.events",
        routingKey = "tenant.created",
        type = "topic"
)
public class TenantQueueConsumer implements MessageListener {
    @Override
    public void onMessage(Message message) {
        System.out.println(message);
    }
}