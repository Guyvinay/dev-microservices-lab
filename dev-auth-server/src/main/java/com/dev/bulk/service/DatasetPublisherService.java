package com.dev.bulk.service;

import com.dev.dto.DatasetUploadedEvent;
import com.dev.utility.TenantContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DatasetPublisherService {

    private static final int BATCH_SIZE = 100000;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public DatasetPublisherService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishDataset() throws Exception {
        String filePath = "/home/guyvinay/dev/repo/dev-microservices-lab/dev-auth-server/src/main/resources/123456_ds_001.csv";
        Path file = Path.of(filePath);
        String tenantId = TenantContextUtil.getTenantId();
        try (
                FileReader fileReader = new FileReader(file.toFile());
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        ) {
            List<String> headers = csvParser.getHeaderNames();
            Iterator<CSVRecord> iterator = csvParser.iterator();

            int batchNumber = 0;
            int rowCount = 0;

            StringBuilder stringBuilder = new StringBuilder();

            while (iterator.hasNext()) {
                CSVRecord csvRecord = iterator.next();
                stringBuilder.append(String.join(",", csvRecord)).append("\n");
                rowCount++;
                if (rowCount % BATCH_SIZE == 0 || !iterator.hasNext()) {
                    batchNumber++;
                    DatasetUploadedEvent event = DatasetUploadedEvent.builder()
                            .tenantId(tenantId)
                            .datasetId(tenantId)
                            .batchNumber(batchNumber)
                            .headers(headers)
                            .totalBatches((int) Math.ceil((double) rowCount / BATCH_SIZE))
                            .build();

                    MessageProperties properties = new MessageProperties();
                    properties.setContentType("application/octet-stream");
                    properties.setHeader("metadata", objectMapper.writeValueAsString(event));

                    byte[] csvByte = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
                    Message message = new Message(csvByte, properties);

                    rabbitTemplate.convertAndSend(
                            "tenant.dataset.exchange",
                            "tenantId.dataset.uploaded",
                            message
                    );
                    System.out.println("TenantId: " + tenantId + ", Published batch: " + batchNumber );
                    stringBuilder.setLength(0);
                }
            }
        }
    }

}
