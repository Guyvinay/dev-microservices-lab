package com.dev.rabbitmq.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.MessageAckListener;

@Slf4j
public class CustomMessageAckListener implements MessageAckListener {

    @Override
    public void onComplete(boolean success, long deliveryTag, Throwable cause) {
        if (success) {
            log.info("ACK received for deliveryTag={}", deliveryTag);
        } else {
            log.error("NACK for deliveryTag={} , cause={}",
                    deliveryTag, cause != null ? cause.getMessage() : "unknown");
        }
    }
}
