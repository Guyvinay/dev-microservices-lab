package com.dev.jooq.utility;

import com.dev.jooq.definition.DynamicColumnDefinition;
import com.dev.jooq.definition.DynamicTableDefinition;
import jakarta.transaction.Transactional;
import org.jooq.CreateTableElementListStep;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Fields;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DynamicDDLExecutor {

    private final DSLContext dsl;
    private final SQLTypeMapper typeMapper;

    public DynamicDDLExecutor(DSLContext dsl,
                              SQLTypeMapper typeMapper) {
        this.dsl = dsl;
        this.typeMapper = typeMapper;
    }

    public void createTable(DynamicTableDefinition table) {

        CreateTableElementListStep step =
                dsl.createTableIfNotExists(table(table));

        step = step.column("id", SQLDataType.UUID.nullable(false));

        if (table.isIncludeAuditColumns()) {

            step = step
                    .column("created_at", SQLDataType.BIGINT)
                    .column("updated_at", SQLDataType.BIGINT);
        }

        for (DynamicColumnDefinition col : table.getColumns()) {

            DataType<?> type = typeMapper.map(col);

            step = step.column(
                    col.getName(),
                    col.isNullable()
                            ? type.nullable(true)
                            : type.nullable(false)
            );
        }

        step.constraint(
                DSL.constraint("pk_" + table.getTableName())
                        .primaryKey("id")
        ).execute();
    }

    private Table<?> table(DynamicTableDefinition table) {

        return DSL.table(
                DSL.name(table.getSchema(), table.getTableName())
        );
    }


    public Set<String> fetchExistingColumns(DynamicTableDefinition table) {

        return dsl.meta()
                .getTables(DSL.name(table.getSchema(), table.getTableName()))
                .stream()
                .flatMap(Fields::fieldStream)
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    public void syncColumns(DynamicTableDefinition table) {

        Set<String> existing = fetchExistingColumns(table);

        for (DynamicColumnDefinition column : table.getColumns()) {

            if (!existing.contains(column.getName())) {

                dsl.alterTable(table(table))
                        .addColumn(
                                column.getName(),
                                typeMapper.map(column)
                        ).execute();
            }
        }
    }


    public void acquireLock(String key) {

        dsl.fetch("SELECT pg_advisory_lock(hashtext(?))", key);
    }

    public void releaseLock(String key) {

        dsl.fetch("SELECT pg_advisory_unlock(hashtext(?))", key);
    }

}
