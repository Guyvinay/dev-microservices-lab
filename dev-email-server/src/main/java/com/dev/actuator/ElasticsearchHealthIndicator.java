package com.dev.actuator;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ElasticsearchHealthIndicator implements HealthIndicator {

    private final RestHighLevelClient client;

    public ElasticsearchHealthIndicator(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public Health health() {
        long start = System.currentTimeMillis();

        try {
            ClusterHealthRequest request = new ClusterHealthRequest();
            request.timeout("2s"); // important: prevent blocking

            ClusterHealthResponse response =
                    client.cluster().health(request, RequestOptions.DEFAULT);

            long latency = System.currentTimeMillis() - start;
            ClusterHealthStatus status = response.getStatus();

            log.info("Elasticsearch health check completed in {} ms with status {}",
                    latency, status);

            if (status == ClusterHealthStatus.RED) {
                log.error("Elasticsearch cluster is RED. Unassigned shards: {}, Active shards: {}",
                        response.getUnassignedShards(),
                        response.getActiveShards());

                return Health.down()
                        .withDetail("clusterStatus", status.name())
                        .withDetail("latency_ms", latency)
                        .withDetail("unassigned_shards", response.getUnassignedShards())
                        .build();
            }

            if (status == ClusterHealthStatus.YELLOW) {
                log.warn("Elasticsearch cluster is YELLOW. Unassigned shards: {}",
                        response.getUnassignedShards());

                return Health.status("DEGRADED")
                        .withDetail("clusterStatus", status.name())
                        .withDetail("latency_ms", latency)
                        .withDetail("unassigned_shards", response.getUnassignedShards())
                        .build();
            }

            return Health.up()
                    .withDetail("clusterStatus", status.name())
                    .withDetail("latency_ms", latency)
                    .build();

        } catch (Exception e) {
            long latency = System.currentTimeMillis() - start;

            log.error("Elasticsearch health check failed after {} ms", latency, e);

            return Health.down(e)
                    .withDetail("latency_ms", latency)
                    .build();
        }
    }

    // Only enable this if you REALLY need periodic logging
     @Scheduled(fixedDelay = 300000)
    public void checkDependencies() {
        Health health = health();
        log.info("Periodic ES health check result: {}", health.getStatus());
    }
}