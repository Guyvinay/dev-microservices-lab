package com.dev.rmq;

import com.dev.rabbitmq.annotation.TenantRabbitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@Slf4j
@TenantRabbitListener(
    value = "AuthAuditListener",
    queue = "tenant.created.sandbox.q", // unique queue
    exchange = "dev.tenant.events",
    routingKey = "dev.tenant.route",
    type = "topic"
)
public class AuthAuditListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        log.info("Sandbox received: {}", new String(message.getBody()));
    }
}