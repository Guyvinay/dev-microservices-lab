package com.dev.rmq.listener;

import com.dev.dto.email.EmailSendEvent;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.dev.service.EmailProcessorService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;

@Slf4j
@TenantRabbitListener(
        value = "RmqEmailEventListener",
        queue = "email.send.server.q",
        exchange = "email.server.exchange",
        routingKey = "email.send.server.q",
        type = "direct",
        prefetch = 1,
        concurrentConsumers = 1,
        maxConcurrentConsumers = 10
)
@RequiredArgsConstructor
public class RmqEmailEventListener implements MessageListener {

    private final EmailProcessorService emailProcessor;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message) {
        try {
            EmailSendEvent emailSendEvent = objectMapper.readValue(message.getBody(), new TypeReference<EmailSendEvent>() {});

            log.info("Received EmailSendEvent: to={}, id={}",
                    emailSendEvent.getTo(),
                    emailSendEvent.getEventId());

            emailProcessor.sendEmail(emailSendEvent);
        } catch (IOException e) {
//            throw new RuntimeException(e);
        }

    }
}
