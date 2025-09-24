package com.dev.rmqConsumer;

import com.dev.rabbitmq.annotation.TenantRabbitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@Slf4j
@TenantRabbitListener(
    value = "AuthNotificationListener",
    queue = "tenant.created.integration.q", // unique queue
    exchange = "dev.tenant.events",
    routingKey = "tenant.created",
    type = "topic"
)
public class AuthNotificationListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        log.info("Integration received: {}", new String(message.getBody()));
    }
}
