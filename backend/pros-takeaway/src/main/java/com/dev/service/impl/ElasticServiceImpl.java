package com.dev.service.impl;

import com.dev.common.dto.document.Document;
import com.dev.dto.ProfilingDocumentDTO;
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
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        try{
            elasticsearchClient.indexDocument(index, student.getStudentId(), new ObjectMapper().writeValueAsString(student));
        } catch (Exception e){
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

        try{
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println(searchResponse);
            rabbitTemplate.convertAndSend("CR_QUEUES", searchResponse);
        } catch ( Exception e) {
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
            for(ProfilingDocumentDTO doc: documents) {
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
            documents = objectMapper.readValue(jsonFile, new TypeReference<>(){});
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
    public List<ProfilingDocumentDTO> getAllProfilingDocuments(String tenantId, Long moduleId, Integer pageNumber, Integer pageSize) {
        return List.of();
    }

    @Override
    public ProfilingDocumentDTO getProfilingDocumentById(String tenantId, Long moduleId, String fieldId, Integer pageNumber, Integer pageSize) {
        return null;
    }
}
