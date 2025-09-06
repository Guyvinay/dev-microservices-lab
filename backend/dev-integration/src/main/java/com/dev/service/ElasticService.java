package com.dev.service;

import com.dev.common.dto.document.Document;
import com.dev.grpc.document.DocumentSearchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ElasticService {

    private static final Logger log = LoggerFactory.getLogger(ElasticService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    public List<Document> getAllDocumentsFromElastic() {
        SearchRequest searchRequest = new SearchRequest("document_index");
        SearchResponse searchResponse;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return mapSearchDocument(searchResponse);
    }

    public List<Document> getDocumentsByQueries(DocumentSearchRequest documentSearchRequest) {
        SearchResponse searchResponse;
        SearchRequest searchRequest = createSearchRequest(documentSearchRequest);
        try {
            searchResponse =  restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("total search-response: {}", searchResponse.getHits().getTotalHits());
        return mapSearchDocument(searchResponse);
    }

    private SearchRequest createSearchRequest(DocumentSearchRequest documentSearchRequest) {
        SearchRequest searchRequest = new SearchRequest("document_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder booleanQueryBuilder = QueryBuilders.boolQuery();

        if(!StringUtils.isEmpty(documentSearchRequest.getUserEmail()) ) {
            booleanQueryBuilder.must(QueryBuilders.termQuery("user.email", documentSearchRequest.getUserEmail()));
        }

        if(!StringUtils.isEmpty(documentSearchRequest.getUserName()) ) {
            booleanQueryBuilder.must(QueryBuilders.matchQuery("user.name", documentSearchRequest.getUserName()));
        }

        if (documentSearchRequest.getUserZipCode() != 0 ) {
            booleanQueryBuilder.must(QueryBuilders.matchQuery("user.address.zip", documentSearchRequest.getUserZipCode()));
        }

        if (!StringUtils.isEmpty(documentSearchRequest.getUserCity())) {
            booleanQueryBuilder.must(QueryBuilders.matchQuery("user.address.city", documentSearchRequest.getUserCity()));
        }

        if (!StringUtils.isEmpty(documentSearchRequest.getUserState())) {
            booleanQueryBuilder.must(QueryBuilders.termQuery("user.address.state", documentSearchRequest.getUserState()));
        }

        String query = booleanQueryBuilder.toString();
        log.info("query: {}", query);
        searchSourceBuilder.query(booleanQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    private List<Document> mapSearchDocument(SearchResponse searchResponse) {
        List<Document> documents = new ArrayList<>();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        if(searchHits.length > 0) {
            Arrays.stream(searchHits).forEach(hit -> {
                documents.add(convertMapToDocument(hit.getSourceAsMap()));
            });
        }
        return documents;
    }

    private Document convertMapToDocument(Map<String, Object> hit) {
        return objectMapper.convertValue(hit, Document.class);
    }


}
