package com.dev.rmqConsumer;

import com.dev.dto.JwtToken;
import com.dev.dto.email.EmailStatusEvent;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtToken jwtToken = (JwtToken) authentication.getDetails();
        log.info("Jwt token: {}", jwtToken);


        try {
            EmailStatusEvent event =
                    objectMapper.readValue(message.getBody(), new TypeReference<EmailStatusEvent>() {});

            log.info("Received EmailStatusEvent id={}, status={}",
                    event.getEventId(),
                    event.getStatus());

        } catch (Exception ex) {
            log.error("Permanent integration failure", ex);
        }
    }
}
