package com.dev.dto.jooqdefinition;


import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Constraint;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.dev.utils.JOOQConstants.VARCHAR;
import static org.jooq.impl.DSL.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TableDefinition {
    private String name;
    private List<ColumnDefinition> columns = new ArrayList<>();

    public Collection<Field<?>> toFields() {
        return columns.stream()
                .map(col -> DSL.field(DSL.name(col.getName()), col.getType()))
                .collect(Collectors.toList());
    }

    public List<Constraint> primaryKeyConstraints() {
        List<Field<?>> pkFields = columns.stream()
                .filter(ColumnDefinition::isPrimaryKey)
                .map(col -> DSL.field(DSL.name(col.getName()), col.getType()))
                .collect(Collectors.toList());

        return pkFields.isEmpty()
                ? Collections.emptyList()
                : List.of(DSL.constraint("pk_" + name).primaryKey(pkFields));
    }
}
