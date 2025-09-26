package com.dev.service.dynamictable;

import com.dev.dto.jooqdefinition.TableDefinition;

public interface DynamicTableService {

    void createTable(String tenantId, TableDefinition tableDefinition);

}