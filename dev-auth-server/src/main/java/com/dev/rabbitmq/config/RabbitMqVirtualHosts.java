package com.dev.rabbitmq.config;

import com.dev.rabbitmq.RabbitMqPublisherProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class RabbitMqVirtualHosts {
    private final RabbitMqPublisherProperties rabbitMqProperties;
    private final RestTemplate restTemplate;
    private final String hostUrl;
    private final String permissionUrl;

    private static final String VHOST_NAME = "public";

    public RabbitMqVirtualHosts(RabbitMqPublisherProperties rabbitMqProperties, RestTemplate restTemplate) {
        this.rabbitMqProperties = rabbitMqProperties;
        this.restTemplate = restTemplate;

        // Example: http://localhost:15672/api/vhosts/
        this.hostUrl = rabbitMqProperties.getManagementBaseUrl() + "/vhosts/";
        this.permissionUrl = rabbitMqProperties.getManagementBaseUrl() + "/permissions/";
    }

    @PostConstruct
    public void initMethod() {
        checkAndCreateVirtualHosts(rabbitMqProperties.getVirtualHost());
    }

    /**
     * Ensures the vhost exists; creates if not present.
     */
    public void checkAndCreateVirtualHosts(String vhostName) {
        log.info("Validating virtual host existence for [{}]", vhostName);
        if (checkVHostAvailability(vhostName)) {
            log.info("Virtual host [{}] already exists", vhostName);
        } else {
            log.info("Virtual host [{}] not found, creating...", vhostName);
            createVirtualHost(vhostName);
//            setPermissions(vhostName, rabbitMqProperties.getUsername());
        }
    }

    public void createVirtualHost(String vhostName) {
        String url = hostUrl + vhostName;
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(rabbitMqProperties.getUsername(), rabbitMqProperties.getPassword());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Virtual host [{}] created successfully", vhostName);
            } else {
                log.error("Failed to create vhost [{}], status={}", vhostName, response.getStatusCode());
            }
        } catch (RestClientResponseException e) {
            log.error("Error creating vhost [{}]: status={}, response={}", vhostName, e.getRawStatusCode(), e.getResponseBodyAsString());
        }
    }

    public boolean checkVHostAvailability(String vhostName) {
        String url = hostUrl + vhostName;
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(rabbitMqProperties.getUsername(), rabbitMqProperties.getPassword());
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.GET, entity, Void.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            log.error("Error checking vhost [{}]", vhostName, e);
            return false;
        }
    }

    /**
     * Grants full permissions to a user for the given vhost.
     */
    public void setPermissions(String vhostName, String user) {
        String url = permissionUrl + vhostName + "/" + user;
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(rabbitMqProperties.getUsername(), rabbitMqProperties.getPassword());
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
            {
              "configure": ".*",
              "write": ".*",
              "read": ".*"
            }
            """;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Permissions granted for user [{}] on vhost [{}]", user, vhostName);
            } else {
                log.error("Failed to set permissions, status={}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error setting permissions for user [{}] on vhost [{}]", user, vhostName, e);
        }
    }
}
