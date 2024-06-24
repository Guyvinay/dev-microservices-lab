package com.pros.service;

import com.pros.annotation.QueueListener;
import com.pros.utils.QueueListeners;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@QueueListener(value = "RmqListener1", queue = QueueListeners.QUEUE2)
public class RmqListener1 implements MessageListener {
    @Override
    public void onMessage(Message message) {

    }
}
