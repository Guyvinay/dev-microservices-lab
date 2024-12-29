package com.dev.dto.jooqdefinition;


import lombok.*;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.dev.utils.JOOQConstants.VARCHAR;
import static org.jooq.impl.DSL.field;

@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TableDefinition {

    private String name;

    private LinkedHashMap<String, ColumnDefinition> columns = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String, ColumnDefinition> getColumns() {
        return columns;
    }

    public void setColumns(LinkedHashMap<String, ColumnDefinition> columns) {
        this.columns = columns;
    }

    public Collection<Field<?>> getColumnsField(DSLContext dslContext) {
        return columns.keySet().stream()
                .map(columnName->
                        field(columnName,
                                columns.get(columnName)
                                        .getDataType(dslContext)
                                )
                ).collect(Collectors.toList());
    }

    public void setVarcharColumns(List<String> columnNames) {
        columns.putAll(
                columnNames.stream()
                        .collect(Collectors.toMap(
                                columnName -> columnName,
                                columnName -> ColumnDefinition.builder()
                                        .name(columnName)
                                        .type(VARCHAR)
                                        .build()
                        ))
        );
    }

    /**
     public void setColumns(Collection<ColumnDefinition> columns) {
     this.columns.putAll(columns.stream()
     .collect(Collectors.toMap(ColumnDefinition::getName,
     Function.identity(),
     (x, y) -> y,
     LinkedHashMap::new)));
     }

     public List<ColumnDefinition> getPks() {
     return columns.values()
     .stream()
     .filter(ColumnDefinition::getPk)
     .collect(Collectors.toList());
     }

     public LinkedHashMap<String, ColumnDefinition> getColumnMap() {
     return columns;
     }

     public Collection<Field<?>> getColumns(DSLContext dslContext) {
     return columns.keySet()
     .stream()
     .map(columnName -> field(columnName,
     columns.get(columnName)
     .getDataType(dslContext)))
     .collect(Collectors.toList());
     }

     public List<Field<?>> getColumns(List<ColumnDefinition> columnDefinitions) {
     return columnDefinitions.stream()
     .map(column -> field(column.getName()))
     .collect(Collectors.toList());
     }

     public LinkedList<String> getColumnNames() {
     return new LinkedList<>(columns.keySet());
     }

     public List<Constraint> getPkConstraints() {
     List<Constraint> constraints = new ArrayList<>();
     List<ColumnDefinition> pks = getPks();

     if (CollectionUtils.isNotEmpty(pks)) {
     List<Field<?>> fields = getColumns(pks);
     constraints.add(constraint(name("pk_" + name)).primaryKey(fields.toArray(Field<?>[]::new)));
     }

     return constraints;
     }

     public Table<?> getTable() {
     return table(name);
     }
     */
    public void setPks(List<String> primaryKeys) {
        primaryKeys.forEach(column -> columns.get(column).setPk(true));
    }
}
