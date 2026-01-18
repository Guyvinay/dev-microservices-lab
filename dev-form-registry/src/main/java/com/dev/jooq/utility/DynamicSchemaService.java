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

    @Transactional
    public void syncTable(DynamicTableDefinition tableDefinition) {

        String lockKey =
                tableDefinition.getSchema()
                        + "." + tableDefinition.getTableName();

        executor.acquireLock(lockKey);

        try {

            executor.createTable(tableDefinition);
            executor.syncColumns(tableDefinition);

        } finally {

            executor.releaseLock(lockKey);
        }
    }

}
