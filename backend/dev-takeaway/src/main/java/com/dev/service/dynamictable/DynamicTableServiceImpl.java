package com.dev.service.dynamictable;

import com.dev.dto.jooqdefinition.TableDefinition;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DynamicTableServiceImpl implements DynamicTableService {

    private DSLContext dslContext;

    @Override
    public void createTable(String tenantId, TableDefinition table) {
//        log.info("Creating table {} if not exists with {} columns", table.getName(), table.getColumns().size());
//        dslContext.createTableIfNotExists(DSL.table(table.getName()))
//                .columns(table.getColumns())
    }
}
