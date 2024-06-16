package com.pros.configuration;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
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
/*
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
*/
    public ConnectionFactory connectionFactory() {
        log.info("Creating connection factory for tenant: 1234");  // Assuming "public" virtual host

        CachingConnectionFactory connectionFactory = createConnectionFactory("/");  // Assuming "/" for single tenant

        try {
            // ... (Optional) Add error handling for connection establishment
            connectionFactory.createConnection();
            return connectionFactory;
        } catch (Exception e) {
            log.error("Failed to create connection for tenant: public", e);
            // Handle connection creation failure (e.g., throw exception)
        }

        // Return null or throw an exception if connection creation fails
        return null;
    }

    private CachingConnectionFactory createConnectionFactory(String virtualHost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitProperties.getHost(), rabbitProperties.getPort());
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }




    public ConnectionFactory connectionFactoryV2() {
        log.info("Creating connection factory for 1234");
        return createConnectionFactoryV2("1234");
    }
    public CachingConnectionFactory createConnectionFactoryV2(String vHost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitProperties.getHost(),rabbitProperties.getPort());
        connectionFactory.setVirtualHost(connectionFactory().getVirtualHost());
        try {
            Connection connection = connectionFactory.createConnection();
            if (connection.isOpen()) {
                log.info("Connection factory created");
                return connectionFactory;
            } else {
                throw new IOException("Failed to open connection");
            }
        } catch (IOException e) {
            log.error("Failed to create connection for tenant: {}" , e.getMessage());
            // Handle connection creation failure (e.g., throw exception)
        }
        // Return null or throw an exception if connection creation fails
        return null;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        log.info("Rabbit template created Bean");
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        log.info("Rabbit Admin Created");
        return new RabbitAdmin(connectionFactory());
    }

}
