package com.dev.rabbitmq.test;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.nio.charset.StandardCharsets;

@Slf4j
@TenantRabbitListener(
        value = "invoiceEventListener",
        queue = "invoice.event.queue",
        exchange = "invoice.exchange",
        routingKey = "invoice.*",   // matches invoice.created, invoice.updated, etc.
        type = "topic"
)
public class InvoiceEventListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("[InvoiceEventListener] tenant={} received: {}",
                message.getMessageProperties().getUserId(), body);
    }
}
