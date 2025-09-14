package com.dev.rabbitmq.test;

import com.dev.rabbitmq.annotation.TenantRabbitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.nio.charset.StandardCharsets;

@Slf4j
@TenantRabbitListener(
        value = "userCreatedListener",
        queue = "user.created.queue",
        exchange = "user.exchange",
        routingKey = "user.created",
        type = "direct",   // exact match routing
        maxConcurrentConsumers = 3,
        prefetch = 5
)
public class UserCreatedListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("[UserCreatedListener] tenant={} received: {}",
                message.getMessageProperties().getUserId(), body);
    }
}
