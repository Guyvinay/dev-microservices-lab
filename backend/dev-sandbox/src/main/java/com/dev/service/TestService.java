package com.dev.service;

import com.dev.exception.DivisionByZeroException;
import com.dev.rmq.wrapper.RabbitTemplateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired
    private RabbitTemplateWrapper rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void pushToQueue(String queueName, Object data) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(data);
        rabbitTemplate.convertAndSend(queueName, message);
    }

    public double divide(double numerator, double denominator) {
        if (denominator == 0) {
            throw new DivisionByZeroException("Cannot divide by zero");
        }
        return numerator / denominator;
    }
}
