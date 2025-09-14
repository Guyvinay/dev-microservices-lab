package com.dev.rabbitmq.test;

import com.dev.rabbitmq.annotation.TenantRabbitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.nio.charset.StandardCharsets;

@Slf4j
@TenantRabbitListener(
        value = "invoiceCreatedListener",       // unique bean name for listener
        queue = "invoice.created.queue",        // queue name
        exchange = "invoice.exchange",          // exchange name (will be declared if not exists)
        routingKey = "invoice.created",         // binding key
        type = "topic",                         // topic exchange
        maxConcurrentConsumers = 5,
        prefetch = 10,
        quorum = true
)
public class InvoiceCreatedListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Received invoice message for tenant={} : {}",
                message.getMessageProperties().getUserId(), body);
    }
}
