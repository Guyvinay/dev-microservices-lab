package com.dev.rmqConsumer;

import com.dev.rabbitmq.annotation.TenantRabbitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@Slf4j
@TenantRabbitListener(
    value = "DatasetEventListenerIntegration",
    queue = "tenantId.dataset.uploaded.integration.q",
    exchange = "tenant.dataset.exchange",
    routingKey = "tenantId.dataset.uploaded.integration.q",
    type = "direct"
)
public class DatasetEventListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        log.info("Integration received: {}", new String(message.getBody()));
    }
}