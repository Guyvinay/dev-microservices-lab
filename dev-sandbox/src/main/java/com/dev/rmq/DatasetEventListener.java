package com.dev.rmq;

import com.dev.dto.DatasetUploadedEvent;
import com.dev.jooq.dto.ColumnDefinition;
import com.dev.jooq.dto.TableDefinition;
import com.dev.jooq.service.DynamicTableService;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Name;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public void onMessage(Message message) {

        try {
            String metadataJson = (String) message.getMessageProperties().getHeaders().get("metadata");
            DatasetUploadedEvent batch = objectMapper.readValue(metadataJson, DatasetUploadedEvent.class);
            String tableName = batch.getTenantId() + "_" + batch.getDatasetId();

            Name name = DSL.name(batch.getTenantId(), tableName);

            if (batch.getBatchNumber() == 1) {
                List<ColumnDefinition> columnDefinitions = new ArrayList<>();

                columnDefinitions.add(
                        ColumnDefinition.builder()
                                .name("id")
                                .type(SQLDataType.BIGINT.identity(true))
                                .primaryKey(true)
                                .build()
                );

                columnDefinitions.addAll(
                        batch.getHeaders().stream()
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

            try (InputStream csvStream = new ByteArrayInputStream(message.getBody())) {
                copyInsertFromStream(name, batch.getHeaders(), csvStream);
            }
            log.info("Sandbox inserted batch {} of {} for dataset {}",
                    batch.getBatchNumber(), batch.getTotalBatches(), batch.getDatasetId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyInsertFromStream(Name table, List<String> headers, InputStream csvStream) {
        String columns = headers.stream()
                .map(col -> "\"" + col + "\"") // safe quoting
                .collect(Collectors.joining(", "));

        String sql = "COPY " + table + " (" + columns + ") FROM STDIN WITH (FORMAT csv)";

        try (Connection conn = DataSourceUtils.getConnection(dataSource)) {
            PGConnection pgConn = conn.unwrap(PGConnection.class);
            CopyManager copyManager = pgConn.getCopyAPI();

            long rows = copyManager.copyIn(sql, csvStream);
            log.info("Inserted {} rows into {}", rows, table);

        } catch (Exception e) {
            log.error("Failed to COPY data into table {}", table, e);
            throw new RuntimeException("COPY insert failed", e);
        }
    }
}