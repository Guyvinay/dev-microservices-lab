package com.dev.jooq.definition;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DynamicTableDefinition {

    private String schema;      // optional
    private String tableName;

    private boolean includeAuditColumns = true;

    private List<DynamicColumnDefinition> columns;
}
