package com.dev.elastic.client;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
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
import org.elasticsearch.client.*;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.elasticsearch.client.indices.CloseIndexResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetFieldMappingsRequest;
import org.elasticsearch.client.indices.GetFieldMappingsResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.AliasMetadata;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ElasticRestHighLevelClient {

    private static final Logger log = LoggerFactory.getLogger(ElasticRestHighLevelClient.class);

    private final String HOST = "localhost";
    private final int PORT = 9200;
    private final String SCHEME = "http";

    private final RestHighLevelClient restHighLevelClient;

    public ElasticRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
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

    public AcknowledgedResponse deleteIndex(DeleteIndexRequest request, RequestOptions options) throws IOException {
        return indicesClient().delete(request, options);
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

    public long countDocs(String index) throws IOException {
        CountRequest req = new CountRequest(index);
        CountResponse res = restHighLevelClient.count(req, RequestOptions.DEFAULT);
        return res.getCount();
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

    public String createIndexWithAlias(String indexName, String aliasName, Map<String, Object> mappings, Map<String, Object> settings, Boolean makeWriteIndex) throws IOException {

        if (!StringUtils.isNotBlank(indexName)) throw new RuntimeException("Index name cannot be blank");

        // ======================================================================
        // 1. Check if index already exists
        // ======================================================================
        if (indexExists(new GetIndexRequest(indexName))) {
            log.warn("Index '{}' already exists — skipping creation.", indexName);
            return "Index already exists. Skipping creation.";
        }

        // ======================================================================
        // 2. Prepare index creation request
        // ======================================================================
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);

        // Attach alias with explicit write index flag
        // Attach alias; control writeIndex flag
        Alias alias = new Alias(aliasName);
        if (makeWriteIndex) alias.writeIndex(true);
        else alias.writeIndex(false);
        createIndexRequest.alias(alias);

        // ======================================================================
        // 3. Apply settings
        // ======================================================================
        if (settings == null || settings.isEmpty()) {
            settings = defaultIndexSettings();
        }
        createIndexRequest.settings(settings);

        // ======================================================================
        // 4. Apply mappings (if provided)
        // ======================================================================
        if (mappings != null && !mappings.isEmpty()) {
            createIndexRequest.mapping(mappings);
        }

        // ======================================================================
        // 5. Execute request
        // ======================================================================
        try {
            CreateIndexResponse response = restHighLevelClient.indices()
                    .create(createIndexRequest, RequestOptions.DEFAULT);

            if (response.isAcknowledged()) {
                log.info("Successfully created index '{}' with alias '{}'.", indexName, aliasName);
                return "Index created successfully.";
            } else {
                log.error("Index creation not acknowledged for '{}'.", indexName);
                return "Index creation request not acknowledged.";
            }

        } catch (ElasticsearchStatusException e) {
            if (e.status().getStatus() == 400) {
                log.error("Bad request while creating index '{}': {}", indexName, e.getMessage(), e);
            } else {
                log.error("Elasticsearch error while creating index '{}': {}", indexName, e.getMessage(), e);
            }
            throw e;
        } catch (IOException e) {
            log.error("I/O error while creating index '{}': {}", indexName, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating index '{}': {}", indexName, e.getMessage(), e);
            throw new RuntimeException("Unexpected error during index creation: " + e.getMessage(), e);
        }
    }

    public void reindexTenantIndex(String tenantAlias, Map<String, Object> mappings, Map<String, Object> settings) throws IOException {

        String currentIndex = resolveIndexNameFromAlias(tenantAlias);

        if (currentIndex == null) {
            throw new IllegalStateException("No physical write index found for alias: " + tenantAlias);
        }
        log.info("Current physical index for alias '{}' is '{}'", tenantAlias, currentIndex);

        String newIndex = inferNewIndexWithNextVersion(currentIndex);

        createIndexWithAlias(newIndex, tenantAlias, mappings, settings, false);

        reindexData(currentIndex, newIndex);

        long currentDocumentCount = countDocs(currentIndex);
        long countAfterReindex = countDocs(newIndex);

        log.info("currentDocumentCount: {}, countAfterReindex: {}", currentDocumentCount, countAfterReindex);

        swapAliasFromIndex(currentIndex, newIndex, tenantAlias);

    }
    public void reindexData(String sourceIndex, String destIndex) throws IOException {
        ReindexRequest reindexRequest = new ReindexRequest();
        reindexRequest.setSourceIndices(sourceIndex);
        reindexRequest.setDestIndex(destIndex);

        // Prevent accidental duplicates on repeated reindex
        reindexRequest.setDestOpType("create");

        reindexRequest.setConflicts("proceed");
        reindexRequest.setRefresh(true);
        reindexRequest.setSlices(4);
        reindexRequest.setDestVersionType(VersionType.INTERNAL);
        reindexRequest.setRequestsPerSecond(1000);

        reindexRequest.setScript(new Script(
                ScriptType.INLINE,
                "painless",
                """
                    // Preserve original ID
                    ctx._id = ctx._id;
    
                    if (ctx._source.conversations == null) {
                        ctx._source.conversations = new ArrayList();
                    }
    
                    Map conv = new HashMap();
                    conv.put("responseType", "DEFAULT_TYPE");
                    conv.put("response", "Auto-generated during migration");
                    conv.put("status", "PENDING");
                    conv.put("responder", "SYSTEM");
                    conv.put("contact", "UNKNOWN");
                    conv.put("dateCreated", null);
                    conv.put("followUpNeeded", false);
    
                    ctx._source.conversations.add(conv);
                """,
                Collections.emptyMap()
        ));

        BulkByScrollResponse response =
                restHighLevelClient.reindex(reindexRequest, RequestOptions.DEFAULT);

        if (!response.getBulkFailures().isEmpty()) {
            throw new RuntimeException("Reindex failures: " + response.getBulkFailures());
        }

        log.info("Total docs reindexed: {}", response.getTotal());
    }
    private void swapAliasFromIndex(String currentIndex, String newIndex, String tenantAlias) throws IOException {
        IndicesAliasesRequest aliasesRequest = new IndicesAliasesRequest();

        IndicesAliasesRequest.AliasActions removeActions = IndicesAliasesRequest.AliasActions.remove().index(currentIndex).alias(tenantAlias);
        IndicesAliasesRequest.AliasActions addAction = IndicesAliasesRequest.AliasActions.add().index(newIndex).alias(tenantAlias).writeIndex(true);

        aliasesRequest.addAliasAction(removeActions);
        aliasesRequest.addAliasAction(addAction);

        AcknowledgedResponse response = restHighLevelClient.indices().updateAliases(aliasesRequest, RequestOptions.DEFAULT);
        if (!response.isAcknowledged()) {
            throw new RuntimeException("Alias swap was not acknowledged");
        }
        log.info("Alias '{}' swapped from '{}' -> '{}'", tenantAlias, currentIndex, newIndex);
    }

    private String inferNewIndexWithNextVersion(String currentIndex) {

        int vIndex = currentIndex.lastIndexOf("_v");
        int versionStr = Integer.parseInt(currentIndex.substring(vIndex + 2)) + 1;
        String newIndex = currentIndex.substring(0, vIndex+2) + versionStr;
        log.info("currentIndex: {}, newIndex: {}", currentIndex, newIndex);
        return newIndex;
    }


    private String resolveIndexNameFromAlias(String tenantAlias) {
        GetAliasesRequest aliasesRequest = new GetAliasesRequest(tenantAlias);
        try {
            GetAliasesResponse aliasesResponse = restHighLevelClient.indices().getAlias(aliasesRequest, RequestOptions.DEFAULT);
            Map<String, Set<AliasMetadata>> aliasMap = aliasesResponse.getAliases();
            for (String index: aliasMap.keySet()) {
                Set<AliasMetadata> metas = aliasMap.get(index);
                for (AliasMetadata aliasMetadata: metas) {
                    Boolean isWriteIndex = aliasMetadata.writeIndex();
                    if (isWriteIndex != null && isWriteIndex) {
                        return index;
                    }
                }
            }
            if(!aliasMap.isEmpty()) {
                return aliasMap.keySet().iterator().next();
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Provides sensible default index settings optimized for balanced
     * performance, compression, and field capacity.
     */
    private Map<String, Object> defaultIndexSettings() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("index.number_of_shards", 3);
        defaults.put("index.number_of_replicas", 1);
        defaults.put("index.refresh_interval", "30s");
        defaults.put("index.mapping.total_fields.limit", 2000);
        defaults.put("index.codec", "best_compression");
        defaults.put("analysis.analyzer.default.type", "standard");
        return defaults;
    }
}
