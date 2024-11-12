package com.dev.service.impl;

import com.dev.common.dto.document.Document;
import com.dev.dto.ProfilingDocumentDTO;
import com.dev.dto.ProfilingDocumentResponse;
import com.dev.exception.ProfilingFailedException;
import com.dev.exception.ProfilingNotFoundException;
import com.dev.rmq.utility.Queues;
import com.dev.rmq.wrapper.RabbitTemplateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dev.configuration.MyElasticsearchClient;
import com.dev.modal.Student;
import com.dev.service.ElasticService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
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
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ElasticServiceImpl implements ElasticService {

    //    @Autowired
    private MyElasticsearchClient elasticsearchClient;

    //    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitTemplateWrapper rabbitTemplateWrapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public static final String index = "localhost_drf_346377_258555_en";

    @Autowired
    private ObjectMapper objectMapper;

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
            rabbitTemplateWrapper.convertAndSend(Queues.QUEUE3, new ObjectMapper().writeValueAsString(searchResponse.getHits().getHits()[0].getSourceAsMap()));
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

    @Override
    public List<ProfilingDocumentDTO> indexBulkDocument() {
        List<ProfilingDocumentDTO> documents = readJsonData();
        log.info("documents: {} ", documents.size());

        BulkRequest bulkRequest = new BulkRequest();

        try {
            for (ProfilingDocumentDTO doc : documents) {
                String docId = doc.getColumn();
                bulkRequest.add(new IndexRequest("localhost_504349_do_profile_346377_en1")
                        .id(docId)
                        .source(objectMapper.writeValueAsString(doc), XContentType.JSON)
                );

                IndexRequest indexRequest = new IndexRequest("localhost_504349_do_profile_346377_en1")
                        .id(docId)
                        .source(objectMapper.writeValueAsString(doc), XContentType.JSON);
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            }
//            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
//            if (bulkResponse.hasFailures()) {
//                System.out.println("Bulk insert had failures: " + bulkResponse.buildFailureMessage());
//            } else {
//                System.out.println("Bulk insert successful.");
//            }

        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            e.printStackTrace();
        }
        return documents;
    }


    public List<ProfilingDocumentDTO> readJsonData() {
        String fileClassPath = "profiling.json";
        List<ProfilingDocumentDTO> documents = null;
        File jsonFile = new File(fileClassPath);


        try {
            documents = objectMapper.readValue(jsonFile, new TypeReference<>() {
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
        }

        return documents;
    }

    @Override
    public List<ProfilingDocumentDTO> getProfilingDocuments(int from, int page) {
        return List.of();
    }

    @Override
    public ProfilingDocumentResponse getAllProfilingDocuments(String tenantId, Long moduleId, Integer pageNumber, Integer pageSize) {
        String indexName = _index(moduleId, tenantId);
        log.info("Getting profiling documents from index: {}", indexName);
        Long totalCount = getTotalDocumentCount(indexName);
        log.info("Total count: {}", totalCount);
        SearchRequest searchRequest = buildSearchRequestWithExclusions(indexName, pageNumber, pageSize, new String[]{"value_frequency"});
        List<ProfilingDocumentDTO> response = fetchProfilingDocuments(searchRequest);

        return new ProfilingDocumentResponse(response, totalCount);

    }

    @Override
    public ProfilingDocumentDTO getProfilingDocumentById(String tenantId, Long moduleId, String fieldId) {

        String indexName = _index(moduleId, tenantId);
        log.info("Getting profiling document from index: {}, for field : {}", indexName, fieldId);
        SearchRequest searchRequest = buildSearchRequestByColumnValue(indexName, fieldId);
        List<ProfilingDocumentDTO> profilingDocumentDTOS = fetchProfilingDocuments(searchRequest);
        if (CollectionUtils.isEmpty(profilingDocumentDTOS)) {
            throw new ProfilingNotFoundException("Technical profiling not found for field: " + fieldId);
        }

        return profilingDocumentDTOS.getFirst();
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
     * Executes elasticsearch search request to retrieve profiling documents, returning a list of
     * {@link ProfilingDocumentDTO} objects.
     *
     * @param searchRequest searchRequest {@link SearchRequest} object contains query for retrieving profiling documents.
     * @return a list {@link List} of {@link ProfilingDocumentDTO} objects each representing a profiling
     * document retrieved from elasticsearch index.
     * If no document is matching the search criteria a empty list is returned.
     */
    private List<ProfilingDocumentDTO> fetchProfilingDocuments(SearchRequest searchRequest) {

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<ProfilingDocumentDTO> results = new ArrayList<>();

            if (searchResponse != null && searchResponse.getHits() != null && searchResponse.getHits().getHits().length > 0) {
                SearchHit[] searchHits = searchResponse.getHits().getHits();
                log.info("Received profiling search response hits : {}", searchHits.length);
                for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                    try {
                        ProfilingDocumentDTO profilingDocument = objectMapper.readValue(searchHit.getSourceAsString(), ProfilingDocumentDTO.class);
                        results.add(profilingDocument);
                    } catch (IOException exception) {
                        log.error("Error while parsing: {}", exception.getMessage());
                    }
                }
            }
            return results;
        } catch (IOException ex) {
            log.error("Exception while fetching profiling: {}", ex.getMessage());
            throw new ProfilingFailedException("Exception while fetching profiling: " + ex.getMessage());
        }

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
