package com.dev.jooq.service;

import com.dev.jooq.dto.TableDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicTableServiceImpl implements DynamicTableService {

    private final DSLContext dsl;

    @Override
    public void createTable(TableDefinition table) {
        log.info("Creating table {}", table.getName());
        dsl.createTableIfNotExists(DSL.name(table.getName()))
                .columns(table.toFields())
                .constraints(table.getPKConstraints())
                .execute();
        log.info("Table {} created (if not exists)", table.getName());
    }

    @Override
    public boolean isTableExists(String tableName) {
        List<Table<?>> table = dsl.meta().getTables(tableName);
        return !table.isEmpty();
    }
}
