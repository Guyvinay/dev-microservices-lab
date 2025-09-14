package com.dev.rabbitmq.test;

import com.dev.rabbitmq.annotation.TenantRabbitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.nio.charset.StandardCharsets;

@Slf4j
@TenantRabbitListener(
        value = "orderPlacedListener",
        queue = "order.placed.queue",
        exchange = "order.exchange",
        routingKey = "order.placed",
        type = "direct"
)
public class OrderPlacedListener  implements MessageListener {

    @Override
    public void onMessage(Message message) {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("[OrderPlacedListener] tenant={} received: {}",
                message.getMessageProperties().getUserId(), body);
    }
}