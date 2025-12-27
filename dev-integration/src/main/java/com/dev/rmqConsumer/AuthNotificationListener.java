package com.dev.rmqConsumer;

import com.dev.hibernate.SchemaInitializer;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.dev.rabbitmq.configuration.RabbitMqManagement;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;

@Slf4j
@TenantRabbitListener(
    value = "AuthNotificationListener",
    queue = "tenant.created.integration.q", // unique queue
    exchange = "dev.tenant.events",
    routingKey = "dev.tenant.route",
    type = "topic"
)
@RequiredArgsConstructor
public class AuthNotificationListener implements MessageListener {

    private final SchemaInitializer schemaInitializer;
    private final ObjectMapper objectMapper;
    private final RabbitMqManagement rabbitMqManagement;

    @Override
    public void onMessage(Message message) {
        try {
            String tenantId = objectMapper.readValue(message.getBody(), new TypeReference<String>() {});
            log.info("Integration received: {}", tenantId);
            schemaInitializer.initialize(tenantId);
            rabbitMqManagement.checkAndCreateVirtualHosts(tenantId);

        } catch (IOException e) {
            log.error("Exception encountered: ", e);
            throw new RuntimeException(e);
        }
    }
}
