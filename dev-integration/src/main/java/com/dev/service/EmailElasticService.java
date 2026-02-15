package com.dev.service;

import com.dev.dto.email.EmailDocument;
import com.dev.dto.email.EmailStatusEvent;
import com.dev.elastic.client.EsRestHighLevelClient;
import com.dev.utility.AuthContextUtil;
import com.dev.utils.ElasticUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailElasticService {

    private final EsRestHighLevelClient esRestHighLevelClient;
    private final ObjectMapper objectMapper;

    @Value("${elastic.index.email}")
    private String index;

    public void bulkIndexEmails(Map<String, EmailDocument> emailDocuments) throws IOException {
        if (emailDocuments.isEmpty()) return;
        log.info("email syncing to elastic start");

        BulkRequest bulkRequest = new BulkRequest();
        String index = _index();

        for (Map.Entry<String, EmailDocument> entry : emailDocuments.entrySet()) {
            String docId = entry.getKey();
            String jsonValue = objectMapper.writeValueAsString(entry.getValue());

            bulkRequest.add(
                    new IndexRequest(index)
                            .id(docId)
                            .source(jsonValue, XContentType.JSON)
            );
        }

        BulkResponse bulkResponse = esRestHighLevelClient.bulkIndexDocument(bulkRequest);
        if (bulkResponse.hasFailures()) {
            log.warn("Bulk insert completed with some failures: {}", bulkResponse.buildFailureMessage());
        } else {
            log.info("Bulk insert successful: {} emails indexed", emailDocuments.size());
        }
    }

    public void indexEmail(EmailDocument emailDocument) throws IOException {
        if (emailDocument == null) return;
        log.info("Indexing email to elastic start: {}", emailDocument.getEmailTo());

        String index = _index();
        String emailId = emailDocument.getEmailTo();
        String jsonValue = objectMapper.writeValueAsString(emailDocument);
        IndexRequest indexRequest = new IndexRequest(index)
                .id(emailId)
                .source(jsonValue, XContentType.JSON)
                .opType(DocWriteRequest.OpType.INDEX);

        IndexResponse indexResponse = esRestHighLevelClient.indexDocument(indexRequest);
        log.info("Email synced to elastic: {}, {}", emailDocument.getEmailTo(), indexResponse.status());
    }

    public List<String> bulkIndexEmail(List<EmailDocument> emailDocuments) throws IOException {
        if (emailDocuments == null || emailDocuments.isEmpty()) return Collections.emptyList();
        List<String> failedToIndex = new ArrayList<>();

        String index = _index(); // your index name logic

        BulkRequest bulkRequest = new BulkRequest();

        for (EmailDocument emailDocument : emailDocuments) {
            if (emailDocument == null) continue;

            String emailId = emailDocument.getEmailTo(); // use unique ID
            String jsonValue = objectMapper.writeValueAsString(emailDocument);

            IndexRequest indexRequest = new IndexRequest(index)
                    .id(emailId)
                    .source(jsonValue, XContentType.JSON)
                    .opType(DocWriteRequest.OpType.INDEX); // upsert behavior

            bulkRequest.add(indexRequest);
        }

        if (bulkRequest.numberOfActions() == 0) return Collections.emptyList();

        BulkResponse bulkResponse = esRestHighLevelClient.bulkIndexDocument(bulkRequest);

        if (bulkResponse.hasFailures()) {
            log.error("Bulk indexing completed with failures: {}", bulkResponse.buildFailureMessage());
        } else {
            log.info("Bulk indexing successful: {} documents", emailDocuments.size());
        }

        for (BulkItemResponse itemResponse : bulkResponse) {
            if (itemResponse.isFailed()) {
                log.error("Failed to index {}: {}", itemResponse.getId(), itemResponse.getFailureMessage());
                failedToIndex.add(itemResponse.getId());
            }
        }
        return failedToIndex;
    }


    private BoolQueryBuilder boolQueryBuilder(long gte, long lte) {
        // 1. Create BoolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 2. MUST conditions.
        boolQueryBuilder.must(QueryBuilders.termQuery("validEmail", true));
        boolQueryBuilder.must(QueryBuilders.termQuery("environment", "development"));
        boolQueryBuilder.must(QueryBuilders.termQuery("resendEligible", true));

        // Retry count <= 5
        RangeQueryBuilder retryRange = QueryBuilders.rangeQuery("retryCount").lte(15);
        boolQueryBuilder.must(retryRange);

        // Status = READY
        boolQueryBuilder.must(QueryBuilders.termsQuery("status.keyword", "FAILED"));

        // 3. MUST NOT condition.
        boolQueryBuilder.mustNot(QueryBuilders.termsQuery("status.keyword", "DISABLED"));

        // 4. FILTER conditions (non-scoring, cached)
        RangeQueryBuilder lastSentAtRangeQuery = QueryBuilders.rangeQuery("lastSentAt").lte(lte).gte(gte);
        boolQueryBuilder.filter(lastSentAtRangeQuery);
        return boolQueryBuilder;
    }

    public List<EmailDocument> getEligibleEmails(long gte, long lte) throws IOException {

        // Initial Steps in this method
        BoolQueryBuilder boolQueryBuilder = boolQueryBuilder(gte, lte);

        // 5. Build SearchSource
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.size(1000);
        sourceBuilder.sort("lastSentAt", SortOrder.DESC);

        log.info("Search query: {}", sourceBuilder);

        // 6. Build SearchRequest
        SearchRequest request = new SearchRequest(_index());
        request.source(sourceBuilder);

        // 7. Execute search
        SearchResponse searchResponse = esRestHighLevelClient.search(request);

        // 8. Get Hits from the response and parse in the desired format.
        return processSearchResponse(searchResponse);
    }

    public long getEligibleEmailsCount(long gte, long lte) throws IOException {
        BoolQueryBuilder boolQueryBuilder = boolQueryBuilder(gte, lte);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(boolQueryBuilder)
                .size(0)
                .trackTotalHits(true);

        log.info("Eligible emails count query: {}", sourceBuilder);

        SearchRequest request = new SearchRequest(_index());
        request.source(sourceBuilder);

        SearchResponse searchResponse = esRestHighLevelClient.search(request);
        return Objects.requireNonNull(searchResponse.getHits().getTotalHits()).value;
    }

    public EmailDocument getEmailDocumentFromESByEmailID(String email) throws IOException {
        try {
            final String index = _index();
            GetRequest getRequest = new GetRequest(index, email);
            GetResponse response = esRestHighLevelClient.getDocument(getRequest);
            if (!response.isExists()) {
                log.warn("No document found in index '{}' for email ID: {}", index, email);
                return null;
            }

            String sourceString = response.getSourceAsString();
            return objectMapper.readValue(sourceString, EmailDocument.class);
        } catch (IOException e) {
            log.error("Error fetching document from Elasticsearch for email ID: {}", email, e);
            throw new RuntimeException("Failed to fetch email document from Elasticsearch", e);
        }
    }

    private String _index() {
        return index;
    }

    public boolean emailDocumentExits(String emailId) throws IOException {
        try {
            GetRequest getRequest = new GetRequest(_index(), emailId);

            // don’t fetch the _source or stored fields
            getRequest.fetchSourceContext(null);
            getRequest.storedFields("_none_");

            // Execute efficient existence check
            boolean exists = esRestHighLevelClient.documentExists(getRequest);

            log.info("Elasticsearch document {} existence check: {}", emailId, exists);
            return exists;
        } catch (IOException e) {
            log.error("Error checking existence for email document with ID: {}", emailId, e);
            throw new RuntimeException("Failed to check email document existence in Elasticsearch", e);
        }
    }

    public List<EmailDocument> getEmailDocumentFromEmailIds(List<String> emailIds) throws IOException {
        if (emailIds == null || emailIds.isEmpty()) {
            log.warn("Email ID list is empty, returning no results.");
            return Collections.emptyList();
        }
        // Ensure index exists + mapping applied
        ensureIndexAndMappingExists();

        TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("emailTo.keyword", emailIds);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(termsQueryBuilder)
//                .sort(SortBuilders.fieldSort("lastSentAt").order(SortOrder.DESC))
                .size(Math.min(emailIds.size(), 1000))
                .trackTotalHits(true); // ensure accurate total count if needed

        log.info("Query to fetch email documents: {}", sourceBuilder);

        SearchRequest searchRequest = new SearchRequest(_index())
                .source(sourceBuilder);

        return processSearchResponse(esRestHighLevelClient.search(searchRequest));
    }

    private void ensureIndexAndMappingExists() throws IOException {
        if(esRestHighLevelClient.indexExists(_index())) return;

        synchronized (this) {
            if(esRestHighLevelClient.indexExists(_index())) return;
            log.info("Creating index and alias: {}", _index());
            createIndexWithAliasAdnMappingV2(_index());
        }

    }

    public String createIndexWithAliasAdnMappingV2(String indexName) throws IOException {


        String alias = aliasNameFromIndexNameV2(indexName);

        Map<String, Object> mappings = ElasticUtility.getEmailDocumentMapping();



        log.info("Index [{}] created with alias [{}]", indexName, alias);

        return esRestHighLevelClient.createIndexWithAlias(indexName, alias, mappings, new HashMap<>(), true);
    }

    private String aliasNameFromIndexNameV2(String indexName) {


        return indexName + "_alias";
    }

    private String indexNameV2(String logicalName) {

        // Example output: email_v1
        return logicalName.toLowerCase() + "_v1";
    }


    private List<EmailDocument> processSearchResponse(SearchResponse searchResponse) {
        if (searchResponse == null || searchResponse.getHits() == null) {
            log.warn("Empty or null response from Elasticsearch.");
            return Collections.emptyList();
        }
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hits = searchHits.getHits();
        log.info("Found {} email documents.", hits.length);

        return Arrays.stream(hits).map((hit) -> {
                    try {
                        return objectMapper.readValue(hit.getSourceAsString(), EmailDocument.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to parse document ID {}: {}", hit.getId(), e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public String createIndexWithAliasAdnMapping(String indexName) throws IOException {
        Map<String, Object> mappings = ElasticUtility.getEmailDocumentMapping();
        String index = indexName(indexName);
        String aliasName = aliasNameFromIndexName(index);
        return esRestHighLevelClient.createIndexWithAlias(index, aliasName, mappings, new HashMap<>(), true);
    }

    public String reindexTenantIndex(String aliasName) throws IOException {
        Map<String, Object> mappings = ElasticUtility.getEmailDocumentMapping();
        esRestHighLevelClient.reindexTenantIndex(indexName(aliasName), mappings, new HashMap<>());
        return "Reindex complete";
    }

    private String indexName(String indexName) {
        String tenantId = AuthContextUtil.getTenantId();
        return tenantId +"_" + indexName;
    }

    private String aliasNameFromIndexName(String indexName) {
        int vIndex = indexName.lastIndexOf("_v");
        if(vIndex>0) {
            return indexName.substring(0, vIndex);
        }
        return indexName + "_read";
    }

    public void updateFromEvent(EmailStatusEvent event) throws IOException {

        try {

            UpdateRequest updateRequest = new UpdateRequest(
                    _index(),
                    event.getEventId()   // document id
            ).doc(event.getUpdateFieldMap());

            UpdateResponse response = esRestHighLevelClient.updateDocument(updateRequest);

            log.info("Updated EmailDocument id={}, result={}",
                    event.getEventId(),
                    response.getResult());
        }  catch (ElasticsearchException e) {

            if (e.status() == RestStatus.NOT_FOUND) {
                log.error("EmailDocument not found for id={}", event.getEventId());
            } else {
                log.error("Failed to update EmailDocument id={}",
                        event.getEventId(), e);
            }

            throw new RuntimeException(e);
        } catch (IOException e) {

            log.error("IO failure updating EmailDocument id={}",
                    event.getEventId(), e);

            throw new RuntimeException(e);
        }

    }

}
/**
 * GET /email_index/_search
 * {
 *   "query": {
 *     "bool": {
 *       "must": [
 *         { "term": { "validEmail": "true" }},
 *         { "term": { "environment": "development" }},
 *         { "term": { "resendEligible": "true" } },
 *         { "range": { "retryCount": { "lte": 5 } }},
 *         { "terms": { "status.keyword": [ "READY" ] } }
 *       ],
 *       "must_not": [
 *         { "term": { "status": "DISABLED" } }
 *       ],
 *       "filter": [
 *         { "range": { "lastSentAt": { "lte": 1760819764409 } }
 *         }
 *       ]
 *     }
 *   },
 *   "sort": [
 *     { "lastSentAt": { "order": "asc" } }
 *   ]
 * }
 */
