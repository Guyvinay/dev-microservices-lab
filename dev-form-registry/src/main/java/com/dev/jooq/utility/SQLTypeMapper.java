package com.dev.jooq.utility;

import com.dev.jooq.definition.DynamicColumnDefinition;
import org.jooq.DataType;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Component;

@Component
public class SQLTypeMapper {

    public DataType<?> map(DynamicColumnDefinition column) {

        return switch (column.getDataType()) {

            case STRING -> SQLDataType.VARCHAR(
                    column.getLength() != null
                            ? column.getLength()
                            : 255
            );

            case TEXT -> SQLDataType.CLOB;

            case NUMBER -> SQLDataType.NUMERIC;

            case BOOLEAN -> SQLDataType.BOOLEAN;

            case DATE -> SQLDataType.DATE;

            case TIMESTAMP -> SQLDataType.TIMESTAMP;

            case UUID -> SQLDataType.UUID;
        };
    }
}
