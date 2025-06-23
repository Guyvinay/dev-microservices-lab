package com.dev.elastic.service;

import com.dev.elastic.client.EsRestHighLevelClient;
import com.dev.webSocket.dto.ChatMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class MessageElasticSyncService {

    private final EsRestHighLevelClient esRestHighLevelClient;
    private final ObjectMapper objectMapper;

    public MessageElasticSyncService(EsRestHighLevelClient esRestHighLevelClient, ObjectMapper objectMapper) {
        this.esRestHighLevelClient = esRestHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Async("threadPoolTaskExecutor")
    public void syncMessageToElastic(ChatMessageDTO messagePayload, String index) throws IOException {
        try {
            log.info("Executing syncMessageToElastic in thread: {}", Thread.currentThread().getName());
            boolean indexExists = esRestHighLevelClient.indexExists(index);
            if(!indexExists) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
                esRestHighLevelClient.createIndex(createIndexRequest);
            }
            IndexRequest request = new IndexRequest(index)
                    .id(messagePayload.getMessageId())
                    .source(objectMapper.writeValueAsString(messagePayload), XContentType.JSON);
            IndexResponse response = esRestHighLevelClient.indexDocument(request);
            log.info("Message  synced to elastic {}", response.status());
        } catch (IOException exception) {
            log.error("Error while syncing message to elastic: ", exception);
        }
    }
}
