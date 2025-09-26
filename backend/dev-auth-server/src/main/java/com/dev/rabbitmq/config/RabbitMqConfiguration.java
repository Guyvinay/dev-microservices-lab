package com.dev.rabbitmq.config;

import com.dev.rabbitmq.RabbitMqPublisherProperties;
import com.dev.rabbitmq.handler.ConfirmCallbackHandler;
import com.dev.rabbitmq.handler.ReturnsCallbackHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(RabbitMqPublisherProperties.class)
public class RabbitMqConfiguration {
    public static final String TENANT_EVENTS_EXCHANGE = "dev.tenant.events";
    public static final String TENANT_ROUTING_KEY = "dev.tenant.route";

    @Bean
    @ConditionalOnMissingBean
    public CachingConnectionFactory cachingConnectionFactory(RabbitMqPublisherProperties props) {
        CachingConnectionFactory cf = new CachingConnectionFactory(props.getHost(), props.getPort());
        cf.setUsername(props.getUsername());
        cf.setPassword(props.getPassword());
        cf.setVirtualHost("public");

        cf.setConnectionTimeout(5000);
        cf.setChannelCheckoutTimeout(30000); // wait for channel from pool
        cf.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        cf.setChannelCacheSize(25); // number of channels cached per connection (tune)

        return cf;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter,
                                         ConfirmCallbackHandler confirmHandler,
                                         ReturnsCallbackHandler returnsHandler) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);

        // may call convertAndSend(exchange, routingKey, message) to send in other exchange
        template.setExchange(TENANT_EVENTS_EXCHANGE);
        template.setRoutingKey(TENANT_ROUTING_KEY);
        template.setMandatory(true); // ensure returned messages are handled

        template.setConfirmCallback(confirmHandler);
        template.setReturnsCallback(returnsHandler);

        template.setMessageConverter(messageConverter);
        return template;
    }
    @Bean
    @DependsOn("rabbitMqVirtualHosts") // bean name of your vhost checker
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true); // will declare exchange automatically
        return admin;
    }

    @Bean
    @ConditionalOnMissingBean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
//    @ConditionalOnMissingBean
    @DependsOn("rabbitMqVirtualHosts")
    public TopicExchange tenantEventsExchange(RabbitAdmin admin) {
        TopicExchange exchange = ExchangeBuilder.topicExchange(TENANT_EVENTS_EXCHANGE).durable(true).build();
        admin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    @DependsOn("rabbitMqVirtualHosts")
    public DirectExchange directExchange(RabbitAdmin rabbitAdmin) {
        DirectExchange directExchange = ExchangeBuilder.directExchange("tenant.dataset.exchange").durable(true).build();
        rabbitAdmin.declareExchange(directExchange);
        return directExchange;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
