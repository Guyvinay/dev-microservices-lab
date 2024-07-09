package com.pros.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pros.configuration.MyElasticsearchClient;
import com.pros.modal.Student;
import com.pros.service.ElasticService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ElasticServiceImpl implements ElasticService {

    @Autowired
    private MyElasticsearchClient elasticsearchClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
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

}
