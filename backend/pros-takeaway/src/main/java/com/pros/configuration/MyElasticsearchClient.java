package com.pros.configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyElasticsearchClient {

    private static final Logger log = LoggerFactory.getLogger(MyElasticsearchClient.class);
    private RestHighLevelClient restHighLevelClient;

    // Replace these with your actual Elasticsearch details
    private final String HOST = "localhost";
    private final int PORT = 9200;
    private final String SCHEME = "http"; // Change to "https" if using SSL

//    @PostConstruct
    public RestHighLevelClient buildElasticClient() {
        // Configure connection details
        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost(HOST, PORT, SCHEME));

        log.info("Connecting to Elasticsearch at {}:{} ({})", HOST, PORT, SCHEME);

        // Build the RestClient builder
        RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[0]));

        // Configure timeouts (optional, adjust as needed)
        builder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(30000));

        // Build the RestHighLevelClient
        this.restHighLevelClient = new RestHighLevelClientBuilder(builder.build()).build();
        log.info("Elasticsearch connection successful...");
        return this.restHighLevelClient;
    }

//    @PreDestroy
    public void destroy() throws IOException {
        log.info("Closing Elastic Connection...");
        this.restHighLevelClient.close();
    }

    // You can add methods here to interact with Elasticsearch using the restHighLevelClient
    // For example, a method to index a document:
    public void indexDocument(String indexName, String documentId, String documentSource) throws IOException {
        IndexRequest request = new IndexRequest(indexName, documentId);
        request.source(documentSource, XContentType.JSON);
        restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }
}
