package com.dev.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.dev.utils.JOOQConstants.*;

@Slf4j
@Component
@AllArgsConstructor
public class DynamicTableUtility {

    public String getDataTypeByColumnName(String columnName) {
        return switch (columnName) {
            case  CREATED_AT, UPDATED_AT -> BIGINT;
            default -> VARCHAR;
        };
    }

}
