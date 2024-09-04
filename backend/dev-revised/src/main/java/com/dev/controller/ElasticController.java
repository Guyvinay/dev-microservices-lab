package com.dev.controller;

import com.dev.elastic.configuration.ElasticConfiguration;
import com.sun.tools.jconsole.JConsoleContext;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/elastic")
public class ElasticController {
/*
    @Autowired
    private ElasticConfiguration elasticConfiguration;

    @PostMapping(value = "/{indexName}")
    public String createIndex(@PathVariable("indexName") String indexName){
        GetIndexRequest indexRequest = new GetIndexRequest(indexName);
        try {
            boolean indexResponse = elasticConfiguration.indexExists(indexRequest);
            if(indexResponse) {
                return "index already exists!!";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CreateIndexResponse indexResponse = null;
        CreateIndexRequest request  = new CreateIndexRequest(indexName);
        request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2).build());
        Map<String, Object> properties = new HashMap<>();

        Map<String, Object> patientId = new HashMap<>();
        patientId.put("type", "keyword");
        properties.put("patientId", patientId);

        Map<String, Object> name = new HashMap<>();
        name.put("type", "text");
        properties.put("name", name);

        Map<String, Object> dateOfBirth = new HashMap<>();
        dateOfBirth.put("type", "date");
        dateOfBirth.put("format", "yyyy-MM-dd");
        properties.put("dateOfBirth", dateOfBirth);

        Map<String, Object> gender = new HashMap<>();
        gender.put("type", "keyword");
        properties.put("gender", gender);

        Map<String, Object> address = new HashMap<>();
        address.put("type", "text");
        properties.put("address", address);

        Map<String, Object> phone = new HashMap<>();
        phone.put("type", "keyword");
        properties.put("phone", phone);

        Map<String, Object> email = new HashMap<>();
        email.put("type", "keyword");
        properties.put("email", email);

        Map<String, Object> medicalHistory = new HashMap<>();
        medicalHistory.put("type", "nested");
        Map<String, Object> medicalHistoryProperties = new HashMap<>();
        Map<String, Object> condition = new HashMap<>();
        condition.put("type", "text");
        medicalHistoryProperties.put("condition", condition);
        Map<String, Object> diagnosisDate = new HashMap<>();
        diagnosisDate.put("type", "date");
        diagnosisDate.put("format", "yyyy-MM-dd");
        medicalHistoryProperties.put("diagnosisDate", diagnosisDate);
        medicalHistory.put("properties", medicalHistoryProperties);
        properties.put("medicalHistory", medicalHistory);

        Map<String, Object> medications = new HashMap<>();
        medications.put("type", "nested");
        Map<String, Object> medicationsProperties = new HashMap<>();
        Map<String, Object> medicationName = new HashMap<>();
        medicationName.put("type", "text");
        medicationsProperties.put("medicationName", medicationName);
        Map<String, Object> dosage = new HashMap<>();
        dosage.put("type", "text");
        medicationsProperties.put("dosage", dosage);
        Map<String, Object> startDate = new HashMap<>();
        startDate.put("type", "date");
        startDate.put("format", "yyyy-MM-dd");
        medicationsProperties.put("startDate", startDate);
        Map<String, Object> endDate = new HashMap<>();
        endDate.put("type", "date");
        endDate.put("format", "yyyy-MM-dd");
        medicationsProperties.put("endDate", endDate);
        medications.put("properties", medicationsProperties);
        properties.put("medications", medications);

        Map<String, Object> allergies = new HashMap<>();
        allergies.put("type", "text");
        properties.put("allergies", allergies);

        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
        request.mapping(mapping);

        try {
            indexResponse = elasticConfiguration.createIndex(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        };
        if(indexResponse != null) {
            System.out.println(indexResponse.isAcknowledged());
        }
        return "success";
    }

    @DeleteMapping(value = "/{indexName}")
    public String deleteIndex(@PathVariable("indexName") String indexName) {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        try {
            boolean indexExists = elasticConfiguration.indexExists(getIndexRequest);
            if(!indexExists) return "index doesn't exists";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            elasticConfiguration.deleteIndex(deleteIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "index deleted";
    }*/
}
