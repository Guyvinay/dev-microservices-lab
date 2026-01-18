package com.dev.jooq.dto;

import com.dev.jooq.definition.DynamicDataType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResolvedFormField {

    private String columnName;

    private DynamicDataType dataType;

    private Integer length;

    private boolean nullable;

    private boolean unique;
}
