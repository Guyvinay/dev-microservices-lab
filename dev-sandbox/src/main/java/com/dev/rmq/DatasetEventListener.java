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
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private final DynamicTableService dynamicTableService;

    @Override
    public void onMessage(Message message) {

        DatasetUploadedEvent datasetUploadedEvent = null;
        try {
            datasetUploadedEvent = objectMapper.readValue(message.getBody(), new TypeReference<DatasetUploadedEvent>() {});
            List<String> columns = datasetUploadedEvent.getRows().getFirst();
            String tableName = datasetUploadedEvent.getTenantId() + "_" + datasetUploadedEvent.getDatasetId();
            List<ColumnDefinition> columnDefinitions = new ArrayList<>();

            columnDefinitions.add(
                    ColumnDefinition.builder()
                            .name("id")
                            .type(SQLDataType.BIGINT.identity(true))
                            .primaryKey(true)
                            .build()
            );

            columnDefinitions.addAll(
                    columns.stream()
                    .map(col-> ColumnDefinition.builder()
                                .name(col)
                                .type(SQLDataType.VARCHAR.length(255))
                                .build()
                    )
                    .toList()
            );

            TableDefinition tableDefinition = TableDefinition.builder()
                    .name(DSL.name(datasetUploadedEvent.getTenantId(), tableName))
                    .columns(columnDefinitions)
                    .build();

//            if (!dynamicTableService.isTableExists(tableName)) {
//                dynamicTableService.createTable(tableDefinition);
//            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Sandbox received");
    }
}