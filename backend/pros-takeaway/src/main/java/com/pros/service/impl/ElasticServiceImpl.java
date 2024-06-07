package com.pros.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pros.modal.Student;
import com.pros.service.ElasticService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ml.GetRecordsRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class ElasticServiceImpl implements ElasticService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public static final String index = "student_record_index";

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void saveStudent(Student student) throws IOException {
//        String id = UUID.randomUUID().toString();

        boolean isExist = checkStudentAvailability(student);
        if (!isExist) {
            deleteExistingStudent(student);
            saveStudentToElastic(student);
        } else {
            saveStudentToElastic(student);
        }
    }

    private void saveStudentToElastic(Student student) throws IOException {
        log.info("saving to elastic ");
        String studentString = new ObjectMapper().writeValueAsString(student);
        IndexRequest indexRequest = new IndexRequest(index);
        indexRequest.id(student.getStudentId());
        indexRequest.source(studentString, XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info("indexResponse {} ", indexResponse.getResult());
    }

    private void deleteExistingStudent(Student student) throws IOException {
        log.info("deleting existing student before saving {}", student.getStudentId());
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(index);
        deleteByQueryRequest.setQuery(QueryBuilders.termQuery("_id", student.getStudentId()));
        deleteByQueryRequest.setTimeout(TimeValue.MINUS_ONE);
        deleteByQueryRequest.setRefresh(true);
        BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        log.info("delete status {}", bulkByScrollResponse.getStatus());
    }

    private boolean checkStudentAvailability(Student student) throws IOException {
        GetRequest getRequest = new GetRequest(index, student.getStudentId());
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        Student studentResponse = new ObjectMapper().convertValue(getResponse, Student.class);
        log.info("student is already availabe at index {}", getResponse);
        System.out.println(studentResponse);
        return studentResponse.getStudentId()!=null;
    }


}
