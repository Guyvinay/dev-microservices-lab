package com.dev.rmq.configuration;

import com.dev.rmq.service.RabbitVirtualHosts;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RabbitBeans {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitProperties rabbitProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RabbitVirtualHosts rabbitVirtualHosts() {
        return new RabbitVirtualHosts(rabbitTemplate, rabbitAdmin, rabbitProperties, restTemplate);
    }

}
