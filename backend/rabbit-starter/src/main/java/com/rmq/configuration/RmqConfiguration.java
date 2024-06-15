package com.rmq.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Configuration
@Slf4j
public class RmqConfiguration {

    @Autowired
    private RabbitProperties rabbitProperties;

    private static final Map<Object, ConnectionFactory> TENANT_CONNECTION_MAP = new ConcurrentHashMap<>();

    private static Set<String> TENANT_IDS = new CopyOnWriteArraySet<>();

    public ConnectionFactory connectionFactory() {
        System.out.println("start creating connection factory");
        TENANT_IDS.add("1234");
        CachingConnectionFactory defaultConnectionFactory = createConnectionFactory("/");
        TENANT_CONNECTION_MAP.put("public", defaultConnectionFactory);


        TENANT_IDS.forEach(tenantId -> {
            CachingConnectionFactory tenantConnectionFactory = createConnectionFactory(tenantId);
            TENANT_CONNECTION_MAP.put(tenantId, tenantConnectionFactory);
        });

        SimpleRoutingConnectionFactory simpleRoutingConnectionFactory = new SimpleRoutingConnectionFactory();
        simpleRoutingConnectionFactory.setTargetConnectionFactories(TENANT_CONNECTION_MAP);
        log.info("connectionFactory ");
        return simpleRoutingConnectionFactory;
    }

    private CachingConnectionFactory createConnectionFactory(String virtualHost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitProperties.getHost(), rabbitProperties.getPort());
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        log.info("Rabbit template created");
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        log.info("Rabbit Admin Created");
        return new RabbitAdmin(connectionFactory());
    }

}
