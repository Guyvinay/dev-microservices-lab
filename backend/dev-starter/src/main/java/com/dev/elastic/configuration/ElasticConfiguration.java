package com.dev.elastic.configuration;

import jakarta.annotation.PostConstruct;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ElasticConfiguration {


    private static final Logger log = LoggerFactory.getLogger(ElasticConfiguration.class);
    private final String HOST = "localhost";
    private final int PORT = 9200;
    private final String SCHEME = "http";

    private RestHighLevelClient restHighLevelClient;

    public ElasticConfiguration() {
    }

    @PostConstruct
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

    public void preDestroy() throws IOException {
        restHighLevelClient.close();
    }

    private IndicesClient indicesClient() {
        return restHighLevelClient.indices();
    }

    public CreateIndexResponse createIndex(CreateIndexRequest request, RequestOptions options) throws IOException {
        return indicesClient().create(request, options);
    }

    public CreateIndexResponse createIndex(CreateIndexRequest request) throws IOException {
        return indicesClient().create(request, RequestOptions.DEFAULT);
    }

    public boolean indexExists(GetIndexRequest request, RequestOptions options) throws IOException {
        return indicesClient().exists(request, options);
    }

    public boolean indexExists(GetIndexRequest request) throws IOException {
        return indicesClient().exists(request, RequestOptions.DEFAULT);
    }

    public AcknowledgedResponse deleteIndex(DeleteIndexRequest request, RequestOptions options) throws IOException {
        return indicesClient().delete(request, options);
    }

}
