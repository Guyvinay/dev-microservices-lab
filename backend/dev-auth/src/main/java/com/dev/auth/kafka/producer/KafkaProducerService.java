package com.dev.auth.kafka.producer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

//@Service
public class KafkaProducerService {


    private final KafkaTemplate<String, String> kafkaTemplate;  //Spring Kafka's helper class for sending messages.

    // Constructor-based dependency injection for KafkaTemplate
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Method to send messages to a Kafka topic
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);  // Sends message to Kafka topic
        System.out.println("Sent message: " + message); // Log the sent message
    }


}
