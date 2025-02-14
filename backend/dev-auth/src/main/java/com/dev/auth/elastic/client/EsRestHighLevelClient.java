package com.dev.auth.elastic.client;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.*;

import java.io.IOException;

@Slf4j
public class EsRestHighLevelClient {

    private final RestHighLevelClient restHighLevelClient;

    public EsRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public IndicesClient getIndicesClient() {
        return restHighLevelClient.indices();
    }

    public boolean indexExists(String indexName) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        return getIndicesClient().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    public CreateIndexResponse createIndex(CreateIndexRequest request) throws IOException {
        return getIndicesClient().create(request, RequestOptions.DEFAULT);
    }

    public CountResponse countDocuments(CountRequest countRequest) throws IOException {
        return restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
    }

    public GetFieldMappingsResponse getFieldMapping(GetFieldMappingsRequest request, RequestOptions requestOptions) throws IOException {
        return getIndicesClient().getFieldMapping(request, requestOptions);
    }



}
