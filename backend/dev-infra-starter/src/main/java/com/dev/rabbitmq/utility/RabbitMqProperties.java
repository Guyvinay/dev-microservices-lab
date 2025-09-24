package com.dev.rabbitmq.utility;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "rmq")
public class RabbitMqProperties {

    /**
     * RabbitMQ host (e.g., localhost, rmq.dev.svc.cluster.local).
     */
    private final String host = "localhost";

    /**
     * RabbitMQ port (default AMQP = 5672).
     */
    private final int port = 5672;

    /**
     * RabbitMQ username for authentication.
     */
    private final String username = "guest";

    /**
     * RabbitMQ password for authentication.
     */
    private final String password = "guest";

    /**
     * RabbitMQ password for authentication.
     */
    private final String addresses = "localhost:15672";

    private final String defaultExchange = "dev.direct";
    private final String defaultType = "direct";
    private final int defaultMaxConcurrentConsumers = 5;
    private final int defaultPrefetch = 10;
    private final boolean defaultQuorum = true;
}