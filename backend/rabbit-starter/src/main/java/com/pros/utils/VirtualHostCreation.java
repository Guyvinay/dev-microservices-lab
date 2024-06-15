package com.pros.utils;

import com.pros.dto.VirtualHostDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Slf4j
public class VirtualHostCreation {

    private RabbitProperties rabbitProperties;

    @Autowired
    private RestTemplate restTemplate;

    private String hostUrl;

    private String username;

    private String password;


    public VirtualHostCreation( RabbitProperties rabbitProperties ) {
        this.rabbitProperties = rabbitProperties;
        this.hostUrl = "http://"+rabbitProperties.getAddresses()+"/api/vhosts/";
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

}
