package com.dev.rmq.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.context.ApplicationContext;

@Slf4j
public class RabbitListenerWrapper implements MessageListener {

    private String tenantId;
    private MessageListener messageListener;
    private ApplicationContext applicationContext;

    public RabbitListenerWrapper(String tenantId, MessageListener messageListener, ApplicationContext applicationContext) {
        this.tenantId = tenantId;
        this.messageListener = messageListener;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onMessage(Message message) {
        try {
            log.info("onMessage RmqListenerWrapper");
            message.getMessageProperties().setUserId(tenantId);
            this.messageListener.onMessage(message);
            log.info("received message from queue {} ", message.getMessageProperties().getConsumerQueue());
        } catch (Exception e) {
            log.info("Exception occurred {} ", e.getMessage());
        }
    }
}
