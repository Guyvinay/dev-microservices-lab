package com.dev.rmqConsumer;

import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;
import java.util.List;

@Slf4j
@TenantRabbitListener(
    value = "DatasetEventListenerIntegration",
    queue = "tenantId.dataset.uploaded.integration.q",
    exchange = "tenant.dataset.exchange",
    routingKey = "tenantId.dataset.uploaded.integration.q",
    type = "direct"
)
@RequiredArgsConstructor
public class DatasetEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    @Override
    public void onMessage(Message message) {
        try {
            List<Object> msg = objectMapper.readValue(message.getBody(), List.class);
            log.info("Integration received: {}", new String(message.getBody()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}