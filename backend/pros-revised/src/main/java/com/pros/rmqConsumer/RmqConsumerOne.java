package com.pros.rmqConsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pros.annotation.QueueListener;
import com.pros.dto.Profile;
import com.pros.utils.QueueListeners;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;

@QueueListener(value = "RmqConsumerOne", queue = QueueListeners.QUEUE1)
public class RmqConsumerOne implements MessageListener {
    @Override
    public void onMessage(Message message) {
        Profile profile = new Profile();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            profile = objectMapper.readValue(message.getBody(), Profile.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(profile);
    }
}
