package com.dev.service.dynamictable;

import com.dev.dto.jooqdefinition.TableDefinition;
import lombok.extern.slf4j.Slf4j;
import org.jooq.CreateTableElementListStep;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DynamicTableServiceImpl implements DynamicTableService {

    private DSLContext dslContext;

    @Override
    public void createTable(String tenantId, TableDefinition table) {
        log.info("Creating table {} if not exists with {} columns", table.getName(), table.getColumns().size());

        CreateTableElementListStep createTableElementListStep =  dslContext.createTableIfNotExists(DSL.table(table.getName()))
                .columns(table.getColumnsField(dslContext))
                .constraints(table.getPrimaryKeyConstraint());

        createTableElementListStep.execute();
        log.info("tables created.");
    }
}
