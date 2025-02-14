package com.dev.auth.elastic;

import com.dev.auth.elastic.properties.ElasticConfigurationProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ElasticSearchConfiguration {
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticConfigurationProperties configurationProperties;

    @PostConstruct
    public RestHighLevelClient restHighLevelClient() {

        // prepare credential provider
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (configurationProperties.getCredentials() != null && StringUtils.isNotBlank(configurationProperties.getCredentials().getUsername()) && StringUtils.isNotBlank(configurationProperties.getCredentials().getPassword())) {
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(configurationProperties.getCredentials().getUsername(), configurationProperties.getCredentials().getPassword()));
        }

        List<HttpHost> httpHosts = new ArrayList<>();
        for (ElasticConfigurationProperties.Elastic elastic : configurationProperties.getProperties()) {
            httpHosts.add(new HttpHost(elastic.getHost(), elastic.getPort(), elastic.getScheme()));
            log.info("Connecting to elasticsearch at ({}://{}:{})", elastic.getScheme(), elastic.getHost(), elastic.getPort());
        }
        log.info("Total hosts in main elastic [{}] ", httpHosts.size());

        RestClientBuilder restClientBuilder = RestClient.builder(httpHosts.toArray(new HttpHost[httpHosts.size()]));

        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> {
            return RequestConfig.custom()
                    .setConnectTimeout(5000) // 5 seconds
                    .setSocketTimeout(RestClientBuilder.DEFAULT_SOCKET_TIMEOUT_MILLIS);
        });

        restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> {
            httpAsyncClientBuilder.setMaxConnPerRoute(RestClientBuilder.DEFAULT_MAX_CONN_PER_ROUTE); // 10
            httpAsyncClientBuilder.setMaxConnTotal(RestClientBuilder.DEFAULT_MAX_CONN_TOTAL); // 30
            // handle based on enable credentials
            if (configurationProperties.getCredentials() != null && StringUtils.isNotBlank(configurationProperties.getCredentials().getUsername()) && StringUtils.isNotBlank(configurationProperties.getCredentials().getPassword())) {
                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider); // set credentials only when required
            }
            return httpAsyncClientBuilder;
        });

        restHighLevelClient = new RestHighLevelClientBuilder(restClientBuilder.build()).setApiCompatibilityMode(true).build();
        log.info("Elastic connection successful...");
        return restHighLevelClient;
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        restHighLevelClient.close();
    }

}
