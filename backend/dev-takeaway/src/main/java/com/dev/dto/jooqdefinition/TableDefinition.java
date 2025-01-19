package com.dev.dto.jooqdefinition;


import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Constraint;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.util.ArrayList;
import java.util.Collection;
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

    public List<Field<?>> getColumns(List<ColumnDefinition> columnDefinitions) {
        return columnDefinitions.stream()
                .map(column-> field(column.getName()))
                .collect(Collectors.toList());
    }

    public void setColumns(LinkedHashMap<String, ColumnDefinition> columns) {
        this.columns = columns;
    }
    public void setColumns(List<ColumnDefinition> columns) {
        this.columns.putAll(
                columns.stream().collect(
                        Collectors.toMap(
                                ColumnDefinition::getName,
                                Function.identity()
                        )
                )
        );
    }

    public Collection<Field<?>> getColumnsField(DSLContext dslContext) {
        return columns.keySet().stream()
                .map(columnName ->
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

    public List<Constraint> getPrimaryKeyConstraint() {
        List<Constraint> constraints = new ArrayList<>();
        List<ColumnDefinition> pks = getPks();

        if (!CollectionUtils.isEmpty(pks)) {
            List<Field<?>> fields = getColumns(pks);
            constraints.add(constraint(name("pk_" + name)).primaryKey(fields.toArray(Field<?>[]::new)));
        }
        return constraints;
    }

    private List<ColumnDefinition> getPks() {
        return columns.values()
                .stream()
                .filter(ColumnDefinition::getPk)
                .collect(Collectors.toList());
    }

    /**
     * public void setColumns(Collection<ColumnDefinition> columns) {
     * this.columns.putAll(columns.stream()
     * .collect(Collectors.toMap(ColumnDefinition::getName,
     * Function.identity(),
     * (x, y) -> y,
     * LinkedHashMap::new)));
     * }
     * <p>
     * public List<ColumnDefinition> getPks() {
     * return columns.values()
     * .stream()
     * .filter(ColumnDefinition::getPk)
     * .collect(Collectors.toList());
     * }
     * <p>
     * public LinkedHashMap<String, ColumnDefinition> getColumnMap() {
     * return columns;
     * }
     * <p>
     * public Collection<Field<?>> getColumns(DSLContext dslContext) {
     * return columns.keySet()
     * .stream()
     * .map(columnName -> field(columnName,
     * columns.get(columnName)
     * .getDataType(dslContext)))
     * .collect(Collectors.toList());
     * }
     * <p>
     * public List<Field<?>> getColumns(List<ColumnDefinition> columnDefinitions) {
     * return columnDefinitions.stream()
     * .map(column -> field(column.getName()))
     * .collect(Collectors.toList());
     * }
     * <p>
     * public LinkedList<String> getColumnNames() {
     * return new LinkedList<>(columns.keySet());
     * }
     * <p>
     * public List<Constraint> getPkConstraints() {
     * List<Constraint> constraints = new ArrayList<>();
     * List<ColumnDefinition> pks = getPks();
     * <p>
     * if (CollectionUtils.isNotEmpty(pks)) {
     * List<Field<?>> fields = getColumns(pks);
     * constraints.add(constraint(name("pk_" + name)).primaryKey(fields.toArray(Field<?>[]::new)));
     * }
     * <p>
     * return constraints;
     * }
     * <p>
     * public Table<?> getTable() {
     * return table(name);
     * }
     */
    public void setPks(List<String> primaryKeys) {
        primaryKeys.forEach(column -> columns.get(column).setPk(true));
    }
}
