package com.dev.rmq.configuration;

import com.dev.rmq.utility.RabbitTenantProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Configuration
@Slf4j
public class RabbitConfig {

    @Autowired
    private RabbitProperties rabbitProperties;

    @Autowired
    private RabbitTenantProvider rabbitTenantProvider;

    public static final Map<Object, ConnectionFactory> TENANT_CONNECTION_MAP = new ConcurrentHashMap<>();

    public static Set<String> TENANT_IDS = new CopyOnWriteArraySet<>();

    @Bean
    public ConnectionFactory connectionFactory() {
        log.info("Connection Factory creation starts.");
        TENANT_IDS = rabbitTenantProvider.getAllTenants();
        log.info("tenants found {}", TENANT_IDS);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitProperties.getHost(), rabbitProperties.getPort());
        connectionFactory.setVirtualHost("/");

        TENANT_CONNECTION_MAP.put("public", connectionFactory);

        TENANT_IDS.forEach(tenant -> {
            CachingConnectionFactory cF = new CachingConnectionFactory(rabbitProperties.getHost(), rabbitProperties.getPort());
            cF.setVirtualHost(tenant);
            TENANT_CONNECTION_MAP.put(tenant, cF);
        });

        SimpleRoutingConnectionFactory simpleRoutingConnectionFactory = new SimpleRoutingConnectionFactory();
        simpleRoutingConnectionFactory.setTargetConnectionFactories(TENANT_CONNECTION_MAP);
        log.info("Connection Factory Creation finish for tenants {}", TENANT_IDS);
        return simpleRoutingConnectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        log.info("RabbitTemplate created");
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        log.info("RabbitAdmin created");
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RestTemplate restTemplate() {
        log.info("RestTemplate bean initialized");
        return new RestTemplate();
    }

}
