package com.dev.actuator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Slf4j
@Component("db")
@RequiredArgsConstructor
public class PostgresHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public Health health() {
        long start = System.currentTimeMillis();
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                long latency = System.currentTimeMillis() - start;
                log.debug("Postgres healthy, latency {} ms", latency);
                return Health.up().withDetail("latency_ms", latency).build();
            } else {
                return Health.down().withDetail("reason", "Connection invalid").build();
            }
        } catch (Exception e) {
            log.error("Postgres health check failed", e);
            return Health.down(e).build();
        }
    }
}