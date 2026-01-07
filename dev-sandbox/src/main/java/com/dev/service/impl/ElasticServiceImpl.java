package com.dev.service.impl;

import com.dev.common.dto.document.Document;
import com.dev.rabbitmq.publisher.RabbitMqPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dev.configuration.MyElasticsearchClient;
import com.dev.modal.Student;
import com.dev.service.ElasticService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ElasticServiceImpl implements ElasticService {

    //    @Autowired
    private MyElasticsearchClient elasticsearchClient;

    //    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
    private RabbitMqPublisher rabbitTemplateWrapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public static final String index = "localhost_drf_346377_258555_en";

    @Autowired
    private ObjectMapper objectMapper;

    public ElasticServiceImpl() {
        if(objectMapper==null) objectMapper = new ObjectMapper();
    }

    @Override
    public void saveStudent(Student student) {
        try {
            elasticsearchClient.indexDocument(index, student.getStudentId(), new ObjectMapper().writeValueAsString(student));
        } catch (Exception e) {
            log.info("parsing error: {}", e.getMessage());
        }
    }

    @Override
    public Student findStudentById(String id) {
        return null;
    }

    @Override
    public void getAllStudents() {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println(searchResponse);
            rabbitTemplate.convertAndSend("CR_QUEUES", searchResponse);
        } catch (Exception e) {
            log.info("error: {}", e.getMessage());
        }

    }

    @Override
    public List<Document> getAllDocument(String index) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchResponse searchResponse;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            rabbitTemplateWrapper.sendToQueue("QUEUE3", new ObjectMapper().writeValueAsString(searchResponse.getHits().getHits()[0].getSourceAsMap()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return mapSearchDocument(searchResponse);
    }

    private List<Document> mapSearchDocument(SearchResponse searchResponse) {
        List<Document> documents = new ArrayList<>();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        if (searchHits.length > 0) {
            Arrays.stream(searchHits).forEach(hit -> {
                documents.add(convertMapToDocument(hit.getSourceAsMap()));
            });
        }
        return documents;
    }

    private Document convertMapToDocument(Map<String, Object> hit) {
        return objectMapper.convertValue(hit, Document.class);
    }


    private void saveStudentToElastic(Student student) throws IOException {
        log.info("saving to elastic ");
        IndexRequest indexRequest = new IndexRequest(index);
        indexRequest.id(student.getStudentId());
//        indexRequest.source(new ObjectMapper().writeValueAsString(student), XContentType.JSON);
//        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    private void deleteExistingStudent(Student student) throws IOException {
        log.info("deleting existing student before saving {}", student.getStudentId());
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(index);
        deleteByQueryRequest.setQuery(QueryBuilders.termQuery("_id", student.getStudentId()));
        deleteByQueryRequest.setTimeout(TimeValue.MINUS_ONE);
        deleteByQueryRequest.setRefresh(true);
//        BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
//        log.info("delete status {}", bulkByScrollResponse.getStatus());
    }

    @Override
    public void indexRequest() {
        /*Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest indexRequest = new IndexRequest("my-index")
                .id("1").source(jsonMap);*/
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.field("user", "kimchy");
                builder.timeField("postDate", new Date());
                builder.field("message", "trying out Elasticsearch");

            }
            builder.endObject();

            IndexRequest indexRequest = new IndexRequest("posts")
                    .id("1").source(builder);

            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

        } catch (Exception e) {
            log.info("{} ", e.getMessage());
            e.getStackTrace();
        }

    }

    /**
     * Builds an Elasticsearch {@link SearchRequest} to search by a specific field value in the specified index.
     *
     * <p> This method constructs a search request targeting a single index, and it allows for querying documents
     * based on the value of a specific field.
     *
     * @param index   the name of the Elasticsearch index to search in.
     * @param fieldId fieldId the name of the field (or column) to filter by,
     * @return a  {@link SearchRequest} that includes a filter for the specified field in the index.
     */
    private SearchRequest buildSearchRequestByColumnValue(String index, String fieldId) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //prepare term query
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("column", fieldId);
        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }
    /**
     * Build an Elasticsearch {@link SearchRequest} with pagination and field exclusion.
     *
     * <p>
     * This method constructs a search request for specified index with support for paginating
     * results and excluding specified fields from the response.     *
     * </p>
     *
     * @param index          name of the ES index which is being queried.
     * @param pageNumber     the page number to retrieve.
     * @param pageSize       number of documents return per page.
     * @param excludedFields an array of field names to be excluded from response. If empty or null no fields are excluded.
     * @return a {@link SearchRequest} that includes paginating results with excluded fields for the specified index.
     */
    private SearchRequest buildSearchRequestWithExclusions(String index, int pageNumber, int pageSize, String[] excludedFields) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from(pageNumber)
                .size(pageSize)
//                .query(QueryBuilders.matchAllQuery())
                .fetchSource(null, excludedFields);

        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }


    /**
     * Gets the total count in specified index.
     * @param indexName name of the index which is being queried for count.
     * @return number of total documents {@link Long}
     */
    public Long getTotalDocumentCount(String indexName) {
        try {
            // Create a CountRequest with the index name
            CountRequest countRequest = new CountRequest(indexName);

            // Perform the count request
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);

            return countResponse.getCount();
        } catch (IOException e) {
            log.error("Exception while getting count: ", e);
            return null;  // Return null in case of failure
        }
    }

    /**
     * prepares the index based on specified moduleId and tenantId.
     *
     * @param moduleId id of the module used to construct index.
     * @param tenantId id of the tenant used to construct index
     * @return a {@link String} representing constructed index name.
     * <code>{serverName}_{moduleId}_do_profile_{tenantId}_en</code>
     */
    private String _index(Long moduleId, String tenantId) {
        return "localhost_" + moduleId + "_do_profile_" + tenantId + "_en";
    }
}
