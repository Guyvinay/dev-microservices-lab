package com.pros.service;

import com.pros.annotation.QueueListener;
import com.pros.utils.Queues;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@QueueListener(value = "RmqListener2", queue = Queues.QUEUE3)
public class RmqListener2 implements MessageListener {
    @Override
    public void onMessage(Message message) {

    }
}
