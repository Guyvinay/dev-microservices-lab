package com.dev.bulk.email.service;

import com.dev.bulk.email.dto.EmailDocument;
import com.dev.elastic.client.EsRestHighLevelClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailElasticService {

    private final EsRestHighLevelClient esRestHighLevelClient;
    private final ObjectMapper objectMapper;

    public List<EmailDocument> getEligibleEmails(long gte,  long lte) throws IOException {
        // 1. Create BoolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 2. MUST conditions.
        boolQueryBuilder.must(QueryBuilders.termQuery("validEmail", true));
        boolQueryBuilder.must(QueryBuilders.termQuery("environment", "development"));
        boolQueryBuilder.must(QueryBuilders.termQuery("resendEligible", true));

        // Retry count <= 5
        RangeQueryBuilder retryRange = QueryBuilders.rangeQuery("retryCount").lte(5);
        boolQueryBuilder.must(retryRange);

        // Status = READY
        boolQueryBuilder.must(QueryBuilders.termsQuery("status.keyword", "READY"));

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

    private String _index() {
        return "email_index";
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
