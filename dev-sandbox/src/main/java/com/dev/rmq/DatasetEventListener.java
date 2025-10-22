package com.dev.rmq;

import com.dev.CustomSchemaInitializer;
import com.dev.dto.DatasetUploadedEvent;
import com.dev.jooq.dto.ColumnDefinition;
import com.dev.jooq.dto.TableDefinition;
import com.dev.jooq.service.DynamicTableService;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.dev.rabbitmq.publisher.RabbitMqPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Name;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.postgresql.util.PSQLException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final DataSource dataSource;
    private final DynamicTableService dynamicTableService;
    private final CustomSchemaInitializer schemaInitializer;
    private final RabbitMqPublisher rabbitMqPublisher;

    @Override
    public void onMessage(Message message) {

        try {
            String metadataJson = (String) message.getMessageProperties().getHeaders().get("metadata");
            DatasetUploadedEvent batch = objectMapper.readValue(metadataJson, DatasetUploadedEvent.class);

            try (InputStream csvStream = new ByteArrayInputStream(message.getBody())) {
                copyInsertFromStream(batch, csvStream, message.getBody());
            }
            log.info("Sandbox inserted batch {} of {} for dataset {}",
                    batch.getBatchNumber(), batch.getTotalBatches(), batch.getDatasetId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void ensureTableExists(Name name, List<String> headers) {
        List<ColumnDefinition> columnDefinitions = new ArrayList<>();

        columnDefinitions.add(
                ColumnDefinition.builder()
                        .name("id")
                        .type(SQLDataType.BIGINT.identity(true))
                        .primaryKey(true)
                        .build()
        );

        columnDefinitions.addAll(
                headers.stream()
                        .map(col -> ColumnDefinition.builder()
                                .name(col)
                                .type(SQLDataType.VARCHAR.length(255))
                                .build()
                        )
                        .toList()
        );

        TableDefinition tableDefinition = TableDefinition.builder()
                .name(name)
                .columns(columnDefinitions)
                .build();

        if (!dynamicTableService.isTableExists(name)) {
            dynamicTableService.createTable(tableDefinition);
        }
    }

    private void copyInsertFromStream(DatasetUploadedEvent uploadedEvent, InputStream csvStream, byte[] body) {
        String tableName = uploadedEvent.getTenantId() + "_" + uploadedEvent.getDatasetId();
        Name table = DSL.name(uploadedEvent.getTenantId(), tableName);
        String schema = table.first();

        if (uploadedEvent.getBatchNumber() == 1) {
            ensureTableExists(table, uploadedEvent.getHeaders());
        }

        String columns = uploadedEvent.getHeaders().stream()
                .map(col -> "\"" + col + "\"")
                .collect(Collectors.joining(", "));

        String sql = "COPY \"" + schema + "\".\"" + tableName + "\" (" + columns + ") FROM STDIN WITH (FORMAT csv)";

        try (Connection conn = DataSourceUtils.getConnection(dataSource)) {
            PGConnection pgConn = conn.unwrap(PGConnection.class);
            CopyManager copyManager = pgConn.getCopyAPI();
            long rows = copyManager.copyIn(sql, csvStream);
            log.info("Inserted {} rows into {}.{}", rows, schema, tableName);
            if(rows >= 0) {
                MessageProperties properties = new MessageProperties();
                properties.setContentType("application/octet-stream");
                properties.setHeader("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJ1c2VySWRcIjpudWxsLFwib3JnXCI6XCJiMzc4YjU4ZC1hYjAwLTQyMmQtOWYzOC0yZDFhZDkzN2U1OTNcIixcIm5hbWVcIjpudWxsLFwiZW1haWxcIjpcInZpa2FzQGdtYWlsLmNvbVwiLFwidGVuYW50SWRcIjpcIjY0MzQ2XCIsXCJjcmVhdGVkRGF0ZVwiOjE3NTg2NTM2MTY2MDEsXCJleHBpcnlEYXRlXCI6MTIxNzU4NjczNDE2NjAxLFwicm9sZXNcIjpbXCIyODc3NDcxMlwiLFwiNDk5NTAwMjRcIixcIjU0MjAwMDk5XCIsXCI2MTUyMDU4OFwiLFwiNjU3MzU5OTJcIixcIjg5NTcwNzEzXCIsXCI5NjMyNDY1N1wiXX0iLCJhdWQiOlsiZGV2LXRha2Vhd2F5IiwiZGV2LXJldmlzZWQiXSwibmJmIjoxNzU4NjczNDE2LCJpc3MiOiJkZXYtYXV0aCIsInBlcm1pc3Npb24iOlsiQURNSU4iLCJVU0VSIiwiTUFOQUdFUiJdLCJleHAiOjEyMTc1ODY3MzQxNiwiaWF0IjoxNzU4NjczNDE2LCJqdGkiOiJiMzY3MWIxZi04YmI1LTQ4NWMtOWZkOC1iYjVhZWM4Y2I5MGEifQ.4MYWXSEd4b7upGoqrdi2toZBIbSA4HGdAxA3XFpwhag");
                properties.setMessageId(String.valueOf(UUID.randomUUID()));
                Message message = new Message(body, properties);
                rabbitMqPublisher.sendToQueueWithMessage("tenant.dataset.exchange","tenantId.dataset.uploaded.integration.q", message);
            }

        } catch (PSQLException psqlException) {
            if (psqlException.getMessage() != null
                    && psqlException.getMessage().contains("schema \"" + schema + "\" does not exist")) {
                log.warn("Schema {} not found, creating it dynamically...", schema);
                schemaInitializer.initialize(schema);
                try {
                    copyInsertFromStream(uploadedEvent, csvStream, body);
                } catch (Exception retryEx) {
                    log.error("Retry COPY failed for {}.{}", schema, tableName, retryEx);
                    throw new RuntimeException("COPY insert failed after schema creation", retryEx);
                }
            } else if (psqlException != null && psqlException.getMessage().contains("relation \"" + schema + "\".\"" + tableName + "\" does not exist")){
                log.warn("Failed to COPY data into table {}.{}", schema, tableName, psqlException);
                try {
                    ensureTableExists(table, uploadedEvent.getHeaders());
                    copyInsertFromStream(uploadedEvent, csvStream, body);
                } catch (Exception retryEx) {
                    log.error("Retry COPY failed for {}.{}", schema, tableName, retryEx);
                    throw new RuntimeException("COPY insert failed after schema creation", retryEx);
                }
            }
        } catch (Exception e) {
            log.error("Unexpected failure during COPY into {}.{}", schema, tableName, e);
            throw new RuntimeException("COPY insert failed", e);
        }
    }
}
