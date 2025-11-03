package com.dev.elastic.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
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
import java.util.HashMap;
import java.util.Map;

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

    public GetResponse getDocument(GetRequest request) throws IOException {
        return restHighLevelClient.get(request, RequestOptions.DEFAULT);
    }

    public boolean documentExists(GetRequest request) throws IOException {
        return restHighLevelClient.exists(request, RequestOptions.DEFAULT);
    }

    public BulkResponse bulkIndexDocument(BulkRequest request) throws IOException {
        return restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
    }

    public CloseIndexResponse closeIndex(CloseIndexRequest request) throws IOException {
        return restHighLevelClient.indices().close(request, RequestOptions.DEFAULT);
    }

    public AcknowledgedResponse deleteIndex(DeleteIndexRequest request) throws IOException {
        return restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
    }

    public String createIndexWithAlias(String indexName, Map<String, Object> mappings, Map<String, Object> settings) throws IOException {
        String aliasName = indexName + "_data";

        boolean exists = indexExists(new GetIndexRequest(indexName));
        if (exists) {
            log.warn("Index {} already exists, skipping creation.", indexName);
            return "Index already exists, skipping creation.";
        }

        if(settings == null || settings.isEmpty()) {
            settings = new HashMap<>();
            settings.put("index.number_of_shards", 3);
            settings.put("index.number_of_replicas", 1);
            settings.put("index.refresh_interval", "30s");
            settings.put("index.mapping.total_fields.limit", 2000);
            settings.put("index.codec", "best_compression");
        }

        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.mapping(mappings);
        createIndexRequest.settings(settings);
        createIndexRequest.alias(new Alias(aliasName));

        CreateIndexResponse response = restHighLevelClient.indices()
                .create(createIndexRequest, RequestOptions.DEFAULT);

        if (response.isAcknowledged()) {
            log.info("Index '{}' created with alias '{}'", indexName, aliasName);
        } else {
            log.error("Failed to create index '{}'", indexName);
        }
        return "Index created";
    }
}
