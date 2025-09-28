package com.dev.jooq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jooq.Constraint;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableDefinition {

    private Name name;
    private List<ColumnDefinition> columns;

    public Collection<Field<?>> toFields() {
        return this.columns.stream()
                .map(column-> DSL.field(DSL.name(column.getName()), column.getType()))
                .collect(Collectors.toList());
    }

    public List<Constraint> getPKConstraints() {
        List<Field<?>> pkFields = this.columns.stream().filter(ColumnDefinition::isPrimaryKey)
                .map(col-> DSL.field(DSL.name(col.getName()), col.getType()))
                .collect(Collectors.toList());

        String tableName = getName().last(); // last part = actual table
        Constraint pk = DSL.constraint("pk_" + tableName)
                .primaryKey(pkFields);

        return List.of(pk);
    }

}
