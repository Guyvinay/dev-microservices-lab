package com.dev.rmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange tenantEventsExchange;

    public void publishTenantCreated(String tenantId) {
        Map<String, Object> event = Map.of(
                "eventType", "TENANT_CREATED",
                "tenantId", tenantId
        );

        rabbitTemplate.convertAndSend("inndev.tenant.events", "tenant.created", event);

        log.info("Published TENANT_CREATED event for tenant {}", tenantId);
    }
}
