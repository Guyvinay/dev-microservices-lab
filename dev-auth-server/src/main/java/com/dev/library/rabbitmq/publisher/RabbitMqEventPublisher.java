package com.dev.library.rabbitmq.publisher;

import com.dev.library.rabbitmq.dto.RmqEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RmqMessagePropertiesFactory propertiesFactory;

    public void publish(RmqEvent event) {
        String correlationId = UUID.randomUUID().toString();
        verifyEvent(event);

        try {
            MessageProperties props =
                    propertiesFactory.create(correlationId);

            Message message = rabbitTemplate
                    .getMessageConverter()
                    .toMessage(event.getPayload(), props);

            CorrelationData correlationData = new CorrelationData(correlationId);

            rabbitTemplate.convertAndSend(
                    event.getExchange(),
                    event.getRoutingKey(),
                    message,
                    correlationData
            );

            log.info(
                    "RMQ published exchange={} rk={} correlationId={}",
                    event.getExchange(),
                    event.getRoutingKey(),
                    correlationId
            );

        } catch (Exception ex) {
            log.error(
                    "RMQ publish failed correlationId={}",
                    correlationId,
                    ex
            );
            throw new IllegalArgumentException("RabbitMQ publish failed", ex);
        }
    }

    private void verifyEvent(RmqEvent event) {
        if(event == null) throw new IllegalArgumentException("Event cannot be null when sending message in RMQ.");
        if (StringUtils.isBlank(event.getExchange())) event.setExchange("default.exchange");
        if (StringUtils.isBlank(event.getRoutingKey())) event.setRoutingKey("default.routing");
    }
}

