package com.dev.jooq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jooq.DataType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColumnDefinition {
    private String name;
    private DataType<?> type;
    private boolean primaryKey;
}
