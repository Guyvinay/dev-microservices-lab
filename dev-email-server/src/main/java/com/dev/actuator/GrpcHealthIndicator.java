package com.dev.actuator;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component("grpc")
public class GrpcHealthIndicator implements HealthIndicator {

    private final Map<String, ManagedChannel> channels;

    // Inject all channels via constructor
    public GrpcHealthIndicator(Map<String, ManagedChannel> channels) {
        this.channels = channels;
    }

    @Override
    public Health health() {
        long startTotal = System.currentTimeMillis();
        Map<String, String> serverStates = new LinkedHashMap<>();
        boolean anyDown = false;
        boolean anyDegraded = false;

        for (Map.Entry<String, ManagedChannel> entry : channels.entrySet()) {
            String serverName = entry.getKey();
            ManagedChannel channel = entry.getValue();
            long start = System.currentTimeMillis();
            try {
                ConnectivityState state = channel.getState(true); // request connection if IDLE
                long latency = System.currentTimeMillis() - start;

                // Map connectivity to health status
                if (state == ConnectivityState.READY) {
                    serverStates.put(serverName, "UP");
                    log.debug("gRPC server '{}' READY, latency {}ms", serverName, latency);
                } else if (state == ConnectivityState.IDLE || state == ConnectivityState.CONNECTING) {
                    serverStates.put(serverName, "DEGRADED");
                    anyDegraded = true;
                    log.warn("gRPC server '{}' not ready ({}), latency {}ms", serverName, state, latency);
                } else {
                    serverStates.put(serverName, "DOWN");
                    anyDown = true;
                    log.error("gRPC server '{}' in bad state {}, latency {}ms", serverName, state, latency);
                }

            } catch (Exception e) {
                serverStates.put(serverName, "DOWN");
                anyDown = true;
                log.error("gRPC health check failed for '{}'", serverName, e);
            }
        }

        long totalLatency = System.currentTimeMillis() - startTotal;

        // Determine overall status
        Health.Builder builder;
        if (anyDown) {
            builder = Health.down();
        } else if (anyDegraded) {
            builder = Health.status("DEGRADED");
        } else {
            builder = Health.up();
        }

        return builder
                .withDetail("servers", serverStates)
                .withDetail("totalLatency_ms", totalLatency)
                .build();
    }
}