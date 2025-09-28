package com.dev.rmq;

import com.dev.hibernate.SchemaInitializer;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;

@Slf4j
@TenantRabbitListener(
    value = "AuthAuditListener",
    queue = "tenant.created.sandbox.q", // unique queue
    exchange = "dev.tenant.events",
    routingKey = "dev.tenant.route",
    type = "topic"
)
@RequiredArgsConstructor
public class AuthAuditListener implements MessageListener {

    private final SchemaInitializer schemaInitializer;
    private final ObjectMapper objectMapper;
    @Override
    public void onMessage(Message message) {
        try {
            String tenantId = objectMapper.readValue(message.getBody(), new TypeReference<String>() {});
            log.info("Sandbox received: {}", tenantId);
            schemaInitializer.initialize(tenantId);
        } catch (IOException e) {
            log.error("Exception encountered: ", e);
            throw new RuntimeException(e);
        }
    }
}