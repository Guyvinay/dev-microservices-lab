package com.dev.dto.jooqdefinition;

import lombok.*;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDataType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnDefinition {
    private String name;
    private DataType<?> type;
    private boolean primaryKey;
}
