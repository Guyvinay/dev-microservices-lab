package com.dev.dto;


import com.dev.entity.FieldTypeEnum;
import lombok.*;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class FieldSeedDefinition {

    private String label;

    private FieldTypeEnum fieldTypeId;

    private String description;

    private Map<String, Object> ui;

    private Map<String, Object> validation;


    private int displayOrder;

}

