package com.dev.jooq.utility;

import com.dev.jooq.definition.DynamicTableDefinition;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DynamicSchemaService {

    private final DynamicDDLExecutor executor;

    public DynamicSchemaService(DynamicDDLExecutor executor) {
        this.executor = executor;
    }

    public void syncTable(DynamicTableDefinition tableDefinition) {

        executor.createTable(tableDefinition);

        executor.syncColumns(tableDefinition);
    }
}
