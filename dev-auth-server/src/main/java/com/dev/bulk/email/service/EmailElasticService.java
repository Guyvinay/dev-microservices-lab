package com.dev.bulk.email.service;

import com.dev.bulk.email.dto.EmailDocument;
import com.dev.elastic.client.EsRestHighLevelClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailElasticService {

    private final EsRestHighLevelClient esRestHighLevelClient;
    private final ObjectMapper objectMapper;

    public void bulkIndexEmails(Map<String, EmailDocument> emailDocuments) throws IOException {
        if (emailDocuments.isEmpty()) return;
        log.info("email syncing to elastic start");

        BulkRequest bulkRequest = new BulkRequest();
        String index = _index();

        for (Map.Entry<String, EmailDocument> entry: emailDocuments.entrySet()) {
            String docId = entry.getKey();
            String jsonValue = objectMapper.writeValueAsString(entry.getValue());

            bulkRequest.add(
                    new IndexRequest(index)
                            .id(docId)
                            .source(jsonValue,  XContentType.JSON)
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
        if (emailDocument==null) return;
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

    public List<EmailDocument> getEligibleEmails(long gte,  long lte) throws IOException {
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
//        boolQueryBuilder.must(QueryBuilders.termsQuery("status.keyword", "READY"));

        // 3. MUST NOT condition.
        boolQueryBuilder.mustNot(QueryBuilders.termsQuery("status.keyword", "DISABLED"));

        // 4. FILTER conditions (non-scoring, cached)
        RangeQueryBuilder lastSentAtRangeQuery = QueryBuilders.rangeQuery("lastSentAt").lte(lte).gte(gte);
        boolQueryBuilder.filter(lastSentAtRangeQuery);

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
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hits = searchHits.getHits();
        log.info("Total Eligible email document: {}", hits.length);
        return Arrays.stream(hits).map((hit)-> {
            try {
                EmailDocument emailDocument = objectMapper.readValue(hit.getSourceAsString(), EmailDocument.class);
                log.info("Email document: {}", emailDocument.getEmailTo());
                return emailDocument;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
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
        }  catch (IOException e) {
            log.error("Error fetching document from Elasticsearch for email ID: {}", email, e);
            throw new RuntimeException("Failed to fetch email document from Elasticsearch", e);
        }
    }

    private String _index() {
        return "email_index";
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
