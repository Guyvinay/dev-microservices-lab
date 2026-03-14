package com.dev.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormPublishedEvent {

    private Long spaceId;
    private Long formId;

    private String tableName;

    private List<FieldSchema> fields;

}