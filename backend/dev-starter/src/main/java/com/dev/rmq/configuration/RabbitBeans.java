package com.dev.rmq.configuration;

import com.dev.rmq.service.RabbitVirtualHosts;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.web.client.RestTemplate;

public class RabbitBeans {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitProperties rabbitProperties;

    @Autowired
    private RestTemplate restTemplate;

    private RabbitVirtualHosts rabbitVirtualHosts() {
        return new RabbitVirtualHosts(rabbitTemplate, rabbitAdmin, rabbitProperties, restTemplate);
    }

}
