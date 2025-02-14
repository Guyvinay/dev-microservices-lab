package com.dev.auth.elastic.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "dev.elastic")
@Data
public class ElasticConfigurationProperties {
    private List<Elastic> properties;
    private MainElasticCredentials credentials;
    /**
     * Main Elastic Properties
     */
    @Data
    public static class Elastic{
        private String host;
        private String scheme;
        private int port;
    }

    @Data
    public static class MainElasticCredentials{
        private String username;
        private String password;
    }

}
