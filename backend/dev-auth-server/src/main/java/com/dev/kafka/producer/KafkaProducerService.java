package com.dev.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;  //Spring Kafka's helper class for sending messages.

    // Method to send messages to a Kafka topic
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);  // Sends message to Kafka topic
        log.info("Sent message: {}", message); // Log the sent message
    }


}
