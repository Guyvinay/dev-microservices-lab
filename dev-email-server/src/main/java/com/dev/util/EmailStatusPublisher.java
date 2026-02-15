package com.dev.util;

import com.dev.dto.email.EmailStatusEvent;
import com.dev.dto.rmq.RmqEvent;
import com.dev.rabbitmq.publisher.RabbitMqPublisher;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailStatusPublisher {

    private final RabbitMqPublisher rabbitMqPublisher;

    private static final String EXCHANGE = "email.status.exchange";
    private static final String ROUTING_KEY = "email.status.server.q";

    public void publish(EmailStatusEvent event) {

        rabbitMqPublisher.publish(
                RmqEvent.builder()
                        .exchange(EXCHANGE)
                        .routingKey(ROUTING_KEY)
                        .payload(event)
                        .build()
        );

        log.info("Published EmailStatusEvent: id={}",
                event.getEventId());
    }
}
