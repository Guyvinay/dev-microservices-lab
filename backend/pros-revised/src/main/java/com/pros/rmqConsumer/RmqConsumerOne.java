package com.pros.rmqConsumer;

import com.pros.annotation.QueueListener;
import com.pros.utils.QueueListeners;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@QueueListener(value = "RmqConsumerOne", queue = QueueListeners.QUEUE1)
public class RmqConsumerOne implements MessageListener {
    @Override
    public void onMessage(Message message) {
        System.out.println(message);
    }
}
