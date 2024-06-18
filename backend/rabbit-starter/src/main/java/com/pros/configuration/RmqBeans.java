package com.pros.configuration;

import com.pros.rmq.service.RmqService;
import com.pros.utils.VirtualHostCreation;
import com.pros.wrapper.RmqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan("com.pros") // Replace with your package name
@Slf4j
public class RmqBeans {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitProperties rabbitProperties;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Bean
    VirtualHostCreation virtualHostCreation() {
        log.info("Virtual Host Bean created");
        return new VirtualHostCreation(rabbitProperties);
    }

    @Bean
    RmqService rmqService() {
        log.info("RmqService bean created");
        return new RmqService(rabbitTemplate, rabbitProperties, restTemplate(), rabbitAdmin);
    }

    @Bean
    RmqWrapper rmqWrapper() {
        log.info("Rmq Wrapper Bean created");
        return new RmqWrapper(rabbitTemplate);
    }

    @Bean
    RestTemplate restTemplate() {
        log.info("Rest Template Bean created");
        return new RestTemplate();
    }


}
