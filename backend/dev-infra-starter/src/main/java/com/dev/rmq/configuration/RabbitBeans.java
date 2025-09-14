package com.dev.rmq.configuration;

import com.dev.rmq.binding.RabbitQueueListenerBinding;
import com.dev.rmq.service.RabbitVirtualHosts;
import com.dev.rmq.wrapper.RabbitTemplateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

//@Configuration
public class RabbitBeans {

    private static final Logger log = LoggerFactory.getLogger(RabbitBeans.class);

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
        log.info("RabbitVirtualHosts bean created.");
        return new RabbitVirtualHosts(rabbitTemplate, rabbitProperties, rabbitAdmin, restTemplate);
    }

    @Bean
    public RabbitTemplateWrapper rabbitTemplateWrapper() {
        log.info("RabbitTemplateWrapper bean created.");
        return new RabbitTemplateWrapper(rabbitTemplate);
    }

    @Bean
    public RabbitQueueListenerBinding rabbitQueueListenerBinding() {
        return new RabbitQueueListenerBinding();
    }

}
