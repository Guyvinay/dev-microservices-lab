package com.dev.rmqConsumer;

import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;

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

    @Override
    public void onMessage(Message message) {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
/*            EmailStatusEvent event =
                    objectMapper.readValue(message.getBody(), EmailStatusEvent.class);

            log.info("Received EmailStatusEvent id={}, status={}",
                    event.getEventId(),
                    event.getStatus());*/

        } catch (Exception ex) {
            log.error("Permanent integration failure", ex);
        }
    }
}
