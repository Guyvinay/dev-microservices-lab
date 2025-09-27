package com.dev.rmq;

import com.dev.dto.DatasetUploadedEvent;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;

@Slf4j
@TenantRabbitListener(
    value = "DatasetEventListenerSandbox",
    queue = "tenantId.dataset.uploaded.sandbox.q",
    exchange = "tenant.dataset.exchange",
    routingKey = "tenantId.dataset.uploaded",
    type = "direct"
)
@RequiredArgsConstructor
public class DatasetEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    @Override
    public void onMessage(Message message) {

        DatasetUploadedEvent datasetUploadedEvent = null;
        try {
            datasetUploadedEvent = objectMapper.readValue(message.getBody(), new TypeReference<DatasetUploadedEvent>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Sandbox received");
    }
}