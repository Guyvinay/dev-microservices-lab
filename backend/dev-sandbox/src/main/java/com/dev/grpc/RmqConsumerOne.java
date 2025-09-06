package com.dev.grpc;


import com.dev.common.dto.Profile;
import com.dev.common.dto.document.Document;
import com.dev.rmq.annotation.RabbitListener;
import com.dev.rmq.utility.Queues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import java.io.IOException;
import java.util.Map;

@RabbitListener(value = "RmqConsumerOne", queue = Queues.QUEUE3)
public class RmqConsumerOne implements MessageListener {

    @Override
    public void onMessage(Message message) {
        Profile profile;
        Document document;
        ObjectMapper objectMapper = new ObjectMapper();
//        MessageProperties messageProperties =  message.getMessageProperties();
//        Map<String, Object> headers =  messageProperties.getHeaders();
        try {
//            profile = objectMapper.readValue(message.getBody(), Profile.class);
            document = objectMapper.readValue(message.getBody(), Document.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(document);
    }
}
