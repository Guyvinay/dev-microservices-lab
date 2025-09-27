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

    private String type;

    @Builder.Default
    private Integer length = 100;

    @Builder.Default
    private Integer precision = -1;

    @Builder.Default
    private Integer scale = -1;

    @Builder.Default
    private Boolean nullable = true;

    @Builder.Default
    private Boolean pk = false;

    private String fk;

//    private void override(DSLContext dsl) {
//        if (dsl.dialect() == SQLDialect.H2 && (GEOMETRY.equals(type))) {
//            type = NCLOB;
//        }
//    }
    public DataType<?> getDataType(DSLContext dslContext) {

        DataType<?> dataType = DefaultDataType.getDataType(SQLDialect.DEFAULT, type);
        if (dataType.isNumeric() && !dataType.hasScale()) {
            dataType = dataType.identity(pk);
        }
        if (length >= 0) {
            dataType = dataType.length(length);
        }
        if (precision >= 0) {
            dataType = dataType.precision(precision);
        }
        if (scale >= 0) {
            dataType = dataType.scale(scale);
        }
        dataType = dataType.nullable(nullable);

        return dataType;

    }
}
