package com.dev.rmqConsumer;

import com.dev.dto.DatasetUploadedEvent;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@TenantRabbitListener(
    value = "DatasetEventListenerIntegration",
    queue = "tenantId.dataset.uploaded.integration.q",
    exchange = "tenant.dataset.exchange",
    routingKey = "tenantId.dataset.uploaded.integration.q",
    type = "direct"
)
@RequiredArgsConstructor
public class DatasetEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    @Override
    public void onMessage(Message message) {
        try {
            String metadataJson = (String) message.getMessageProperties().getHeaders().get("metadata");
            DatasetUploadedEvent batch = objectMapper.readValue(metadataJson, DatasetUploadedEvent.class);

            try (
                    InputStream inputStream = new ByteArrayInputStream(message.getBody());
                    Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            ) {
                List<String> headers = batch.getHeaders(); // may be null for non-first batches
                Iterator<CSVRecord> it = csvParser.iterator();
                List<Map<String, Object>> list = new ArrayList<>();
                while (it.hasNext()) {
                    CSVRecord rec = it.next();
                    Map<String, Object> doc = new LinkedHashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        String key = headers.get(i);
                        String value = i < rec.size() ? rec.get(i) : null;
                        doc.put(key, value);
                    }
                    list.add(doc);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}