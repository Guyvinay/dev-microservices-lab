package com.dev.utility;

import com.dev.rmq.annotation.RabbitListener;
import com.dev.rmq.utility.Queues;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

@RabbitListener(value = "RabbitListenerOne", queue = Queues.QUEUE1)
public class RabbitListenerOne implements MessageListener {
    @Override
    public void onMessage(Message message) {
        System.out.println(message);
    }
}
