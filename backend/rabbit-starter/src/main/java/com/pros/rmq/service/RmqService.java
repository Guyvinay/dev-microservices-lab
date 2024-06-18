package com.pros.rmq.service;

import com.pros.RabbitStarterApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class RmqService {

    private static final Logger log = LoggerFactory.getLogger(RmqService.class);
    private String hostUrl = "http://localhost:15672/api";

    private String username;

    private String password;

    private RabbitProperties rabbitProperties;

    private RabbitTemplate rabbitTemplate;

    private final RestTemplate restTemplate;

    private final RabbitAdmin rabbitAdmin;

    public RmqService(RabbitTemplate rabbitTemplate, RabbitProperties rabbitProperties, RestTemplate restTemplate, RabbitAdmin rabbitAdmin) {
        this.restTemplate = restTemplate;
        this.rabbitAdmin = rabbitAdmin;
    }
    public void createVirtualHost(String vHost) {

        String url = hostUrl + "/vhosts" + vHost;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth("guest", "guest");
        httpHeaders.set("Content-Type", "application/json");
        Map<String, Object> body = new HashMap<>();
        body.put("description", "vHost Description");
        body.put("metadata", "metadata");

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> httpResponse =  restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);

        if(httpResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Virtual Host created");
//            createQueuesAndBindings(vHost);
        } else {
            log.info("Virtual host creation failed");
        }

    }

    private void createQueuesAndBindings(String vHost) {
        String[] queues = {"queue1", "queue2", "queue3", "queue4", "queue5"};
        String exchange = "topic-exchange";

    }
    public boolean checkVHost(String tenantId) {
        ResponseEntity<?> responseValidate = null;

        try {
            String Url = hostUrl + tenantId;
            log.info("Url {}", Url);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBasicAuth("guest", "guest");
            HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);  // No body for GET

            responseValidate = restTemplate.exchange(
                    Url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {}
            );
            log.info("response entity {}", responseValidate);

            if (responseValidate.getStatusCode() == HttpStatus.OK) {
                return true;
            } else {
                log.error("Failed to check virtual host: Status code {}", responseValidate.getStatusCodeValue());
                return false;  // Or throw a specific exception for clarity
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Virtual host not found: {}", e.getMessage());
            return false;
        } catch (RestClientResponseException e) {
            log.error("Error checking virtual host: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error checking virtual host: {}", e.getMessage());
            return false;
        }
    }

    public void createVirtualHostV2(String vhostName) {
        if(checkVHost(vhostName)){
            log.info("virtual host already exists");
        }else {
            String url = hostUrl + "/vhosts/" + vhostName;

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth("guest", "guest");
            Map<String, Object> body = new HashMap<>();
            body.put("description", "vHost Description");
            body.put("metadata", "metadata");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.PUT, entity, new ParameterizedTypeReference<Void>() {
            });

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Virtual host created successfully");
            } else {
                throw new RuntimeException("Failed to create virtual host: " + response.getStatusCode());
            }
        }
    }

}
