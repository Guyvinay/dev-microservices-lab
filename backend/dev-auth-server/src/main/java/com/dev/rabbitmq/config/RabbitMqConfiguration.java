package com.dev.rabbitmq.config;

import com.dev.rabbitmq.RabbitMqPublisherProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(RabbitMqPublisherProperties.class)
public class RabbitMqConfiguration {
    public static final String TENANT_EVENTS_EXCHANGE = "dev.tenant.events";

    @Bean
    public CachingConnectionFactory publisherConnectionFactory(RabbitMqPublisherProperties props) {
        CachingConnectionFactory ccf = new CachingConnectionFactory(props.getHost(), props.getPort());
        ccf.setUsername(props.getUsername());
        ccf.setPassword(props.getPassword());
        ccf.setVirtualHost(props.getVirtualHost());
        // production tuning
        ccf.setChannelCacheSize(25);
        ccf.setConnectionTimeout(10_000);
        // if TLS: configure ccf.getRabbitConnectionFactory()...
        return ccf;
    }
    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory, RabbitMqPublisherProperties props,
                                         ObjectMapper objectMapper) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMandatory(props.isPublisherReturns()); // triggers ReturnCallback when undeliverable
        if (props.isPublisherConfirms()) {
            // for modern Spring Boot / Spring AMQP, set publisher-confirms in properties if using caching CF.
            // Also set a confirm callback here
            template.setConfirmCallback((CorrelationData correlationData, boolean ack, String cause) -> {
                if (!ack) {
                    // log and take action (persist failed message, alert, retry...)
                    // correlationData may be null if not provided
                    // Example: log.error("Publish confirm failed: {}", cause);
                }
            });
        }

        if (props.isPublisherReturns()) {
            template.setReturnsCallback(returnedMessage -> {
                // handle returned messages
                // Example: log.warn("Returned message: {}", returnedMessage);
            });
        }

        // optional: use Jackson to convert payloads to JSON automatically
        template.setMessageConverter(new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter(objectMapper));
        return template;
    }
    @Bean
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true); // will declare exchange automatically
        return admin;
    }

    @Bean
    public TopicExchange tenantEventsExchange(RabbitAdmin admin) {
        // declare durable topic exchange for tenant lifecycle events
        TopicExchange exchange = ExchangeBuilder.topicExchange(TENANT_EVENTS_EXCHANGE).durable(true).build();
        // ensure declaration (RabbitAdmin will pick up at context startup)
        admin.declareExchange(exchange);
        return exchange;
    }
}
