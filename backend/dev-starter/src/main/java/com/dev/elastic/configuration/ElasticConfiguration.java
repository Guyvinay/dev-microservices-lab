package com.dev.elastic.configuration;

import com.dev.elastic.client.ElasticRestHighLevelClient;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ElasticConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ElasticConfiguration.class);

    private RestHighLevelClient restHighLevelClient;

    private final String HOST = "localhost";
    private final int PORT = 9200;
    private final String SCHEME = "http";

    @Bean
    public RestHighLevelClient buildElasticClient() {
        log.info("Connecting to elastic starts");
        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost(HOST, PORT, SCHEME));
        log.info("Connecting to elasticsearch at ({}://{}:{})", SCHEME, HOST, PORT);
        RestClientBuilder restClientBuilder = RestClient.builder((HttpHost[]) httpHosts.toArray(new HttpHost[httpHosts.size()]));
        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(30000));
        this.restHighLevelClient = new RestHighLevelClientBuilder(restClientBuilder.build()).build();
        log.info("Connection to elastic successful ");
        return this.restHighLevelClient;
    }

    @Bean
    public ElasticRestHighLevelClient elasticRestHighLevelClient() {
        return new ElasticRestHighLevelClient(restHighLevelClient);
    }

}
