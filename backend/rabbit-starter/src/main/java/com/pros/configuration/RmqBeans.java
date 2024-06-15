package com.pros.configuration;

import com.pros.utils.VirtualHostCreation;
import com.pros.wrapper.RmqWrapper;
import lombok.extern.slf4j.Slf4j;
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

    @Bean
    VirtualHostCreation virtualHostCreation() {
        log.info("Virtual Host Creation Bean");
        return new VirtualHostCreation(rabbitProperties);
    }

    @Bean
    RmqWrapper rmqWrapper() {
        log.info("Rmq Wrapper Bean");
        return new RmqWrapper(rabbitTemplate);
    }

    @Bean
    RestTemplate restTemplate() {
        log.info("Rest Template Bean");
        return new RestTemplate();
    }


}
