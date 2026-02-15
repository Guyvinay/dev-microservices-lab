package com.dev.rmqConsumer;

import com.dev.dto.email.EmailStatusEvent;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.dev.service.EmailElasticService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@Slf4j
@TenantRabbitListener(
        value = "EmailStatusEventListener",
        queue = "email.status.server.q",
        exchange = "email.status.exchange",
        routingKey = "email.status.server.q",
        type = "direct",
        prefetch = 1,
        concurrentConsumers = 1,
        maxConcurrentConsumers = 10
)
@RequiredArgsConstructor
public class EmailStatusEventListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final EmailElasticService emailElasticService;

    @Override
    public void onMessage(Message message) {

        try {

            EmailStatusEvent event =
                    objectMapper.readValue(
                            message.getBody(),
                            EmailStatusEvent.class
                    );

            emailElasticService.updateFromEvent(event);

            log.info("Processed EmailStatusEvent id={}",
                    event.getEventId());

        } catch (Exception ex) {

            log.error("Permanent integration failure", ex);
            throw new RuntimeException(ex); // Let DLQ handle it
        }
    }
}
