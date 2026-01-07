package com.dev.elastic.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "dev.elastic")
public class ElasticConfigurationProperties {

    private Elastic property;
//    private List<Elastic> properties;
    private MainElasticCredentials credentials;

    /**
     * Main Elastic Properties
     */
    @Data
    public static class Elastic {
        private String host;
        private String scheme;
        private int port;
    }

    @Data
    public static class MainElasticCredentials {
        private String username;
        private String password;
    }

}
