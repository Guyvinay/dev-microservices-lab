package com.dev.jooq.definition;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DynamicColumnDefinition {

    private String name;

    private DynamicDataType dataType;

    private Integer length;

    private boolean nullable;

    private boolean unique;

    private Object defaultValue;
}
