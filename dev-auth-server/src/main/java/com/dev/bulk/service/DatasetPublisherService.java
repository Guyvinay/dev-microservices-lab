package com.dev.bulk.service;

import com.dev.dto.DatasetUploadedEvent;
import com.dev.utility.TenantContextUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DatasetPublisherService {

    private static final int BATCH_SIZE = 10000;
    private final RabbitTemplate rabbitTemplate;

    public DatasetPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
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

            List<List<String>> batch = new LinkedList<>();

            while (iterator.hasNext()) {
                if(batch.isEmpty()) batch.add(headers);
                CSVRecord csvRecord = iterator.next();
                List<String> rows = csvRecord.toList();
                batch.add(rows);
                rowCount++;
                if (batch.size() == BATCH_SIZE || !iterator.hasNext()) {
                    batchNumber++;
                    DatasetUploadedEvent event = DatasetUploadedEvent.builder()
                            .tenantId(tenantId)
                            .datasetId(tenantId)
                            .batchNumber(batchNumber)
                            .totalBatches((int) Math.ceil((double) rowCount / BATCH_SIZE))
                            .rows(batch)
                            .build();

                    rabbitTemplate.convertAndSend(
                            "tenant.dataset.exchange",
                            "tenantId.dataset.uploaded",
                            event
                    );
                    System.out.printf("Published batch %d with %d rows%n", batchNumber, batch.size());
                    batch.clear();
                }
            }
        }
    }

}
