package com.dev.rmq.service;

import com.dev.rmq.utility.RabbitTenantProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class RabbitVirtualHosts {


    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;
    private final RabbitProperties rabbitProperties;
    private final RestTemplate restTemplate;

    @Autowired
    private RabbitTenantProvider rabbitTenantProvider;

    public RabbitVirtualHosts(RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin, RabbitProperties rabbitProperties, RestTemplate restTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
        this.rabbitProperties = rabbitProperties;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void postConstruct() {

    }


}
