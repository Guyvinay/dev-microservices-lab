package com.dev.library.rabbitmq.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfirmCallbackHandler implements RabbitTemplate.ConfirmCallback {

    // Called after broker ack/nack for a message
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData == null ? "null" : correlationData.getId();
        if (ack) {
            log.info("Message ACKed by broker correlationId={}", id);
            // Mark event published in outbox (if using outbox) or remove from pending store
        } else {
            log.error("Message NACKed correlationId={} cause={}", id, cause);
            // Take action: mark for retry, persist to retries table, alert
        }
    }
}