package com.dev.auth.elastic.service;

import com.dev.auth.elastic.client.EsRestHighLevelClient;
import com.dev.auth.webSocket.dto.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class MessageElasticSyncService {

    private final EsRestHighLevelClient esRestHighLevelClient;
    private final ObjectMapper objectMapper;

    public MessageElasticSyncService(EsRestHighLevelClient esRestHighLevelClient, ObjectMapper objectMapper) {
        this.esRestHighLevelClient = esRestHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Async("elasticsearchExecutor")
    public void syncMessageToElastic(ChatMessage chatMessage, String index) throws IOException {
        log.info("Message received to sync");
        try {
            String messageId = UUID.randomUUID().toString();
            log.info("Executing syncMessageToElastic in thread: {}", Thread.currentThread().getName());
            boolean indexExists = esRestHighLevelClient.indexExists(index);
            if(!indexExists) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
                esRestHighLevelClient.createIndex(createIndexRequest);
            }
            IndexRequest request = new IndexRequest(index)
                    .id(messageId)
                    .source(new ObjectMapper().writeValueAsString(chatMessage), XContentType.JSON);
            IndexResponse response = esRestHighLevelClient.indexDocument(request);
            log.info("Message  synced to elastic");
        } catch (IOException exception) {
            log.error("Error while syncing message to elastic: ", exception.getStackTrace());
        }

    }


}
