package com.dev.elastic.client;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.ReindexRequest;

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

    public BulkByScrollResponse reIndex(ReindexRequest reindexRequest) throws IOException {
        return restHighLevelClient.reindex(reindexRequest, RequestOptions.DEFAULT);
    }

    public SearchResponse search(SearchRequest request) throws IOException {
        return restHighLevelClient.search(request, RequestOptions.DEFAULT);
    }

    public CountResponse count(CountRequest request) throws IOException {
        return restHighLevelClient.count(request, RequestOptions.DEFAULT);
    }

    public SearchResponse scroll(SearchScrollRequest request) throws IOException {
        return restHighLevelClient.scroll(request, RequestOptions.DEFAULT);
    }

    public boolean indexExists(GetIndexRequest request) throws IOException {
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    public GetSettingsResponse getSettings(GetSettingsRequest request) throws IOException {
        return restHighLevelClient.indices().getSettings(request, RequestOptions.DEFAULT);
    }

    public GetMappingsResponse getMappings(GetMappingsRequest request) throws IOException {
        return restHighLevelClient.indices().getMapping(request, RequestOptions.DEFAULT);
    }

    public IndexResponse indexDocument(IndexRequest request) throws IOException {
        return restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    public CloseIndexResponse closeIndex(CloseIndexRequest request) throws IOException {
        return restHighLevelClient.indices().close(request, RequestOptions.DEFAULT);
    }

    public AcknowledgedResponse deleteIndex(DeleteIndexRequest request) throws IOException {
        return restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
    }
}
