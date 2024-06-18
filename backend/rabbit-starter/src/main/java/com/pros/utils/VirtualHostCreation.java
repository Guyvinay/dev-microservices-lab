package com.pros.utils;

import com.pros.dto.VirtualHostDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
public class VirtualHostCreation {

    private RabbitProperties rabbitProperties;

    @Autowired
    private RestTemplate restTemplate;
//    @Value("${rabbitmq.management.url}")
    private String managementUrl = "http://localhost:15672/api";

//    @Value("${rabbitmq.management.username}")
    private String userName = "guest";

//    @Value("${rabbitmq.management.password}")
    private String passWord = "guest";

    private String hostUrl;

    private String username;

    private String password;

    @PostConstruct
    public void init() {
        System.out.println("RabbitMQ Management URL: " + managementUrl);
        System.out.println("RabbitMQ Management Username: " + username);
        System.out.println("RabbitMQ Management Password: " + password);
    }

    public VirtualHostCreation( RabbitProperties rabbitProperties ) {
        log.info("Rabbit Properties: {}", rabbitProperties);
        this.rabbitProperties = rabbitProperties;
//        this.hostUrl = "http://"+rabbitProperties.getAddresses()+"/api/vhosts/";
        this.hostUrl = "http://localhost:15672/api/vhosts/";
        this.username = rabbitProperties.getUsername();
        this.password = rabbitProperties.getPassword();
    }

    private final static String tags = "*";
    private final static String CLASSIC = "classic";

    public void createVirtualHost(String tenantId) {
        ResponseEntity<?> responseEntity = null;

        try {

            String url = hostUrl + tenantId;
            VirtualHostDto virtualHostDto = new VirtualHostDto();
            virtualHostDto.setName(tenantId);
            virtualHostDto.setDescription(tenantId);
            List<String> tags =  new ArrayList<>();
            virtualHostDto.setTags(tags);
            virtualHostDto.setDefaultqueuetype(CLASSIC);


            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));

            HttpEntity<?> httpEntity = new HttpEntity<Object>(virtualHostDto, httpHeaders);

            responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, new ParameterizedTypeReference<>() {
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void createVirtualHostWithMetadata(String virtualHostName, String description, List<String> tags) throws RestClientResponseException {
        String url = hostUrl + virtualHostName;

        VirtualHostDto virtualHostDto = new VirtualHostDto();
        virtualHostDto.setName(virtualHostName);
        virtualHostDto.setDescription(description);
        virtualHostDto.setTags(tags);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<VirtualHostDto> httpEntity = new HttpEntity<>(virtualHostDto, httpHeaders);

        restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Void.class);
        log.info("Virtual host '" + virtualHostName + "' created successfully with metadata.");
    }

    public boolean checkVHost(String tenantId) {
        ResponseEntity<?> responseValidate = null;

        try {
            String Url = hostUrl + tenantId;
            log.info("Url {}", Url);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBasicAuth(userName, passWord);
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
            String url = managementUrl + "/vhosts/" + vhostName;

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(userName, passWord);

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
