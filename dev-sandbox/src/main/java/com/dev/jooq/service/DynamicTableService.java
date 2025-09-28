package com.dev.jooq.service;

import com.dev.jooq.dto.TableDefinition;
import org.jooq.Name;

public interface DynamicTableService {
    void createTable(TableDefinition table);
    boolean isTableExists(Name name);
}
