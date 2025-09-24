package com.dev.rabbitmq.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReturnsCallbackHandler implements RabbitTemplate.ReturnsCallback {
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        Message message = returned.getMessage();
        log.error("Message returned replyCode={} replyText={} exchange={} routingKey={} messageId={}",
                returned.getReplyCode(), returned.getReplyText(), returned.getExchange(), returned.getRoutingKey(),
                message.getMessageProperties().getMessageId());
        // Persist returned message to DB for inspection/retry
    }

    // fallback older signature:
    public void onReturnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("Returned (legacy) replyCode={} replyText={} exchange={} routingKey={} messageId={}",
                replyCode, replyText, exchange, routingKey, message.getMessageProperties().getMessageId());
    }
}