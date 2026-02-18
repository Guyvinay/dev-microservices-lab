package com.dev.rabbitmq.configuration;

import com.dev.provider.JwtTokenProviderManager;
import com.dev.rabbitmq.listener.TenantRabbitListenerBinding;
import com.dev.rabbitmq.publisher.RabbitMqPublisher;
import com.dev.rabbitmq.utility.RabbitMqProperties;
import com.dev.utility.RabbitTenantProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableConfigurationProperties(RabbitMqProperties.class)
@Slf4j
public class RabbitMqConfiguration {

    public static final Map<Object, ConnectionFactory> TENANT_CONNECTION_MAP = new ConcurrentHashMap<>();

    public static final Set<String> TENANT_IDS = ConcurrentHashMap.newKeySet();

    @Autowired
    private RabbitMqProperties rabbitMqProperties;

    @Autowired
    private RabbitTenantProvider rabbitTenantProvider;

    @Bean
    public ConnectionFactory connectionFactory() {
        log.info("Initializing RabbitMQ tenant connections...");

        TENANT_IDS.addAll(rabbitTenantProvider.getAllTenants());
        log.info("Loaded tenant IDs: {}", TENANT_IDS);

        // Tenant-specific connections
        TENANT_IDS.forEach(tenantId -> {
            CachingConnectionFactory ccf = createTenantConnectionFactory(tenantId);
            TENANT_CONNECTION_MAP.put(tenantId, ccf);
            log.info("Registered RabbitMQ connection for tenantId={}", tenantId);
        });

        SimpleRoutingConnectionFactory routingConnectionFactory = new SimpleRoutingConnectionFactory();
        routingConnectionFactory.setTargetConnectionFactories(TENANT_CONNECTION_MAP);
        routingConnectionFactory.setDefaultTargetConnectionFactory(TENANT_CONNECTION_MAP.get("public"));

        log.info("RabbitMQ routing connection factory initialized successfully");
        return routingConnectionFactory;
    }

    private CachingConnectionFactory createTenantConnectionFactory(String vHost) {
        CachingConnectionFactory ccf = new CachingConnectionFactory();
        ccf.setUsername(rabbitMqProperties.getUsername());
        ccf.setPassword(rabbitMqProperties.getPassword());
        ccf.setHost(rabbitMqProperties.getHost());
        ccf.setVirtualHost(vHost);

        // production-safe defaults
        ccf.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        ccf.setChannelCacheSize(25);
        ccf.setConnectionLimit(10);

        ccf.getRabbitConnectionFactory().setMaxInboundMessageBodySize(200 * 1024 * 1024); // size to 200 MB

        return ccf;
    }


    /**
     * RabbitAdmin automatically declares queues, exchanges, and bindings
     * on application startup using the provided ConnectionFactory.
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        log.info("Creating RabbitAdmin bean with connectionFactory={}", connectionFactory.getClass().getSimpleName());
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * Configured RabbitTemplate for publishing messages.
     * Includes a MessageConverter for JSON serialization and optional publisher confirms/returns.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        log.info("Creating RabbitTemplate bean");

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);



/* will enable later in a separate class

        // Enable mandatory flag for returns (important in publisher confirms mode)
        rabbitTemplate.setMandatory(true);

        // Publisher confirms
        rabbitTemplate.setConfirmCallback((correlation, ack, cause) -> {
            if (ack) {
                log.debug("Message confirmed by broker [correlationId={}]",
                        correlation != null ? correlation.getId() : "null");
            } else {
                log.error("Message NOT confirmed [correlationId={}, cause={}]",
                        correlation != null ? correlation.getId() : "null", cause);
            }
        });

        // Returned messages when exchange/routingKey is invalid or no queue is bound
        rabbitTemplate.setReturnsCallback(returned -> {
            log.warn("Message returned [exchange={}, routingKey={}, replyCode={}, replyText={}, correlationId={}]",
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getMessage().getMessageProperties().getCorrelationId());
        });
*/

        log.info("RabbitTemplate initialized with JSON converter and publisher callbacks enabled");
        return rabbitTemplate;
    }

    /**
     * RestTemplate for invoking RabbitMQ HTTP API (e.g., to create vhosts, users, permissions).
     */
    @Bean
    public RestTemplate restTemplate() {
        log.info("RestTemplate bean initialized for RabbitMQ management API calls");
        return new RestTemplate();
    }

    /**
     * Helper component for managing RabbitMQ virtual hosts per tenant.
     */
    @Bean
    public RabbitMqManagement rabbitMqVirtualHosts(
            RabbitMqProperties rabbitMqProperties,
            RestTemplate restTemplate
    ) {
        log.info("Creating RabbitMqVirtualHosts manager");
        return new RabbitMqManagement(rabbitMqProperties, restTemplate);
    }

    /**
     * Registers and binds tenant-specific listeners based on @TenantRabbitListener annotations.
     */
    @Bean
    public TenantRabbitListenerBinding tenantRabbitListenerBinding(
            ApplicationContext applicationContext,
            RabbitAdmin rabbitAdmin,
            RabbitMqManagement rabbitMqManagement,
            JwtTokenProviderManager jwtTokenProviderManager
    ) {
        log.info("Creating TenantRabbitListenerBinding for dynamic tenant-based listener registration");
        return new TenantRabbitListenerBinding(applicationContext, rabbitAdmin, rabbitMqManagement,  jwtTokenProviderManager);
    }

    /**
     * Publisher bean for tenant-aware message publishing.
     */
    @Bean
    public RabbitMqPublisher rabbitMqPublisher(RabbitTemplate rabbitTemplate, RmqMessagePropertiesFactory propertiesFactory) {
        log.info("Creating RabbitMqPublisher for tenant-aware publishing");
        return new RabbitMqPublisher(rabbitTemplate, propertiesFactory);
    }

    @Bean
    public RmqMessagePropertiesFactory rmqMessagePropertiesFactory() {
        return new RmqMessagePropertiesFactory();
    }

    /**
     * JSON message converter using Jackson.
     * Ensures all messages are serialized/deserialized as JSON automatically.
     */
    @Bean
    public MessageConverter jacksonMessageConverter() {
        log.info("Creating Jackson2JsonMessageConverter for RabbitMQ messages");
        return new Jackson2JsonMessageConverter();
    }
}
