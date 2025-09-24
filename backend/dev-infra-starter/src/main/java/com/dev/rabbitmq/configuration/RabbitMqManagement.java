package com.dev.rabbitmq.configuration;

import com.dev.rabbitmq.utility.RabbitMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class RabbitMqManagement {

    private final RabbitMqProperties rabbitMqProperties;
    private final String hostUrl;
    private final RestTemplate restTemplate;

    public RabbitMqManagement(RabbitMqProperties rabbitMqProperties,
                              RestTemplate restTemplate) {
        this.rabbitMqProperties = rabbitMqProperties;
        this.restTemplate = restTemplate;

        // RabbitMQ Management API base URL for vhosts
        this.hostUrl = "http://" + rabbitMqProperties.getAddresses() + "/api/vhosts/";
    }
/*

    @PostConstruct
    public void initMethod() {

        Set<String> tenants = RabbitMqConfiguration.TENANT_IDS;
        tenants.forEach(this::checkAndCreateVirtualHosts);
    }
*/

    public void checkExistingBindingForQueueAndDelete(
            String vHost, String exchange, String queue, String expectedRoutingKey) {

        List<Map<String, Object>> bindings = getAllBindingsForQueueInExchange(vHost, exchange, queue);

        if (bindings.isEmpty()) {
            log.info("No existing bindings found for queue {} in exchange {}", queue, exchange);
            return;
        }

        String encodedVhost = UriUtils.encodePathSegment(vHost, StandardCharsets.UTF_8);
        String encodedQueue = UriUtils.encodePathSegment(queue, StandardCharsets.UTF_8);

        for (Map<String, Object> binding : bindings) {
            String sourceExchange = (String) binding.get("source");
            String routingKey = (String) binding.get("routing_key");
            String propertiesKey = (String) binding.get("properties_key");

            // Only care about bindings for our exchange that don't match the expected routing key
            if (exchange.equals(sourceExchange) && !expectedRoutingKey.equals(routingKey)) {
                try {
                    // Build DELETE URL
                    String deleteUrl = String.format("http://%s/api/bindings/%s/e/%s/q/%s/%s",
                            rabbitMqProperties.getAddresses(),
                            encodedVhost,
                            sourceExchange,
                            encodedQueue,
                            propertiesKey);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    headers.setBasicAuth(rabbitMqProperties.getUsername(), rabbitMqProperties.getPassword());
                    HttpEntity<Void> entity = new HttpEntity<>(headers);

                    restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, Void.class);

                    log.info("Deleted old binding: queue={}, exchange={}, routingKey={}", queue, exchange, routingKey);
                } catch (Exception e) {
                    log.error("Failed to delete old binding for queue={} exchange={} routingKey={}",
                            queue, exchange, routingKey, e);
                }
            }
        }
    }

    public List<Map<String, Object>> getAllBindingsForQueueInExchange(String vHost, String exchange, String queue) {
        try {
            String encodedVhost = UriUtils.encodePathSegment(vHost, StandardCharsets.UTF_8);
            String encodedQueue = UriUtils.encodePathSegment(queue, StandardCharsets.UTF_8);

            String url = String.format("http://%s/api/queues/%s/%s/bindings",
                    rabbitMqProperties.getAddresses(), encodedVhost, encodedQueue);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setBasicAuth(rabbitMqProperties.getUsername(), rabbitMqProperties.getPassword());

            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

            log.info("Fetching bindings from URL: {}", url);

            ResponseEntity<List<Map<String, Object>>> responseEntity =
                    restTemplate.exchange(url, HttpMethod.GET, httpEntity,
                            new ParameterizedTypeReference<>() {});

            List<Map<String, Object>> bindings = responseEntity.getBody();
            log.info("Bindings found for queue {}: {}", queue, bindings != null ? bindings.size() : 0);
            return bindings != null ? bindings : Collections.emptyList();

        } catch (Exception e) {
            log.error("Failed to fetch bindings for queue {} in exchange {}", queue, exchange, e);
            return Collections.emptyList();
        }
    }


    /**
     * Ensures a vhost exists for the given tenant.
     * If not found, a new vhost will be created.
     *
     * @param tenantId The tenant identifier (vhost name).
     */
    public void checkAndCreateVirtualHosts(String tenantId) {
        log.info("Validating virtual host existence for tenant={}", tenantId);
        if (checkVHostAvailability(tenantId)) {
            log.info("Virtual host [{}] already exists", tenantId);
        } else {
            log.info("Virtual host [{}] not found, creating...", tenantId);
            createVirtualHost(tenantId);
        }
    }


    /**
     * Creates a new RabbitMQ virtual host.
     *
     * @param vhostName The virtual host name.
     */
    public void createVirtualHost(String vhostName) {
        log.info("Starting virtual host creation for [{}]", vhostName);

        String url = hostUrl + vhostName;
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(rabbitMqProperties.getUsername(), rabbitMqProperties.getPassword());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Virtual host [{}] created successfully", vhostName);
            } else {
                log.error("Failed to create virtual host [{}], status={}", vhostName, response.getStatusCode());
                throw new IllegalStateException("Failed to create virtual host: " + response.getStatusCode());
            }
        } catch (RestClientResponseException e) {
            log.error("Error creating virtual host [{}]: status={}, response={}", vhostName, e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error creating virtual host: " + vhostName, e);
        } catch (Exception e) {
            log.error("Unexpected error while creating vhost [{}]", vhostName, e);
            throw new RuntimeException("Unexpected error creating vhost: " + vhostName, e);
        }
    }
    /**
     * Checks if a given vhost exists in RabbitMQ.
     *
     * @param vHost The virtual host name.
     * @return true if vhost exists, false otherwise.
     */
    public boolean checkVHostAvailability(String vHost) {
        String url = hostUrl + vHost;
        log.debug("Checking virtual host availability at [{}]", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(rabbitMqProperties.getUsername(), rabbitMqProperties.getPassword());
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Void.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Virtual host [{}] is available", vHost);
                return true;
            } else {
                log.warn("Unexpected status while checking vhost [{}]: {}", vHost, response.getStatusCode());
                return false;
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Virtual host [{}] not found", vHost);
            return false;
        } catch (RestClientResponseException e) {
            log.error("Error checking virtual host [{}]: status={}, response={}", vHost, e.getRawStatusCode(), e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error checking virtual host [{}]", vHost, e);
            return false;
        }
    }
}
