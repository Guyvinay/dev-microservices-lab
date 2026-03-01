package com.dev.actuator;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component("rabbit")
@RequiredArgsConstructor
public class RabbitHealthIndicator implements HealthIndicator {

    private final ConnectionFactory connectionFactory;

    @Override
    public Health health() {
        long start = System.currentTimeMillis();
        try (Connection connection = connectionFactory.createConnection()) {
            if (connection != null && connection.isOpen()) {
                long latency = System.currentTimeMillis() - start;
                log.debug("RabbitMQ connection healthy, latency {} ms", latency);
                return Health.up()
                        .withDetail("latency_ms", latency)
                        .withDetail("version", connection.getDelegate().getServerProperties().get("version"))
                        .build();
            } else {
                return Health.down().withDetail("reason", "Connection closed").build();
            }
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - start;
            log.error("RabbitMQ health check failed", e);
            return Health.down(e)
                    .withDetail("latency_ms", latency)
                    .build();
        }
    }
}