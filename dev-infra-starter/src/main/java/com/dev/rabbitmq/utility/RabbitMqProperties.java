package com.dev.rabbitmq.utility;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "rmq")
public class RabbitMqProperties {

    /**
     * RabbitMQ host (e.g., localhost, rmq.dev.svc.cluster.local).
     */
    private String host = "localhost";

    /**
     * RabbitMQ port (default AMQP = 5672).
     */
    private int port = 5672;

    /**
     * RabbitMQ username for authentication.
     */
    private String username = "guest";

    /**
     * RabbitMQ password for authentication.
     */
    private String password = "guest";

    /**
     * RabbitMQ password for authentication.
     */
    private String addresses = "localhost:15672";

    private String defaultExchange = "dev.direct";
    private String defaultType = "direct";
    private int defaultMaxConcurrentConsumers = 5;
    private int defaultPrefetch = 10;
    private boolean defaultQuorum = true;
}