package com.dev.jooq.service;

import com.dev.jooq.dto.TableDefinition;

public interface DynamicTableService {
    void createTable(TableDefinition table);
    boolean isTableExists(String tableName);
}
