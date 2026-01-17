package com.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormFieldDto {

    private UUID id;
    private UUID formId;
    private UUID fieldId;
    private Integer fieldOrder;
    private Boolean requiredOverride;
    private String overrideJson;

    // getters and setters
}