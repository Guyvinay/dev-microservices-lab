package com.dev.rabbitmq;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.rabbit")
@Getter
@Setter
public class RabbitMqPublisherProperties {
    private String host = "localhost";
    private int port = 5672;
    private String username = "guest";
    private String password = "guest";
    private String virtualHost = "public";    // public vhost where lifecycle exchange lives
    private String tenantUserPrefix = "tenant-"; // naming scheme for per-tenant user (optional)
    private boolean publisherConfirms = true;
    private boolean publisherReturns = true;

    // management API
    private String managementBaseUrl = "http://localhost:15672/api";
    private String managementUser = "guest";
    private String managementPassword = "guest";

}