package com.dev.bulk.service;

import com.dev.bulk.dto.DatasetUploadedEvent;
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
        String filePath = "/home/guyvinay/dev/repo/dev-microservices-lab/backend/dev-auth-server/src/main/resources/123456_ds_001.csv";
        Path file = Path.of(filePath);
        try (
                FileReader fileReader = new FileReader(file.toFile());
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        ) {
            List<String> headers = csvParser.getHeaderNames();
            Iterator<CSVRecord> iterator = csvParser.iterator();

            int batchNumber = 0;
            int rowCount = 0;

            List<Map<String, String>> batch = new ArrayList<>(BATCH_SIZE);

            while (iterator.hasNext()) {
                CSVRecord csvRecord = iterator.next();
                Map<String, String> row = new HashMap<>();
                for (String header : headers) {
                    row.put(header, csvRecord.get(header));
                }
                batch.add(row);
                rowCount++;
                if (batch.size() == BATCH_SIZE || !iterator.hasNext()) {
                    batchNumber++;
                    DatasetUploadedEvent event = DatasetUploadedEvent.builder()
                            .tenantId("123456")
                            .datasetId("123456")
                            .batchNumber(batchNumber)
                            .totalBatches((int) Math.ceil((double) rowCount / BATCH_SIZE))
                            .rows(new ArrayList<>(batch))
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

            System.out.println(batch);
        }
    }

}
