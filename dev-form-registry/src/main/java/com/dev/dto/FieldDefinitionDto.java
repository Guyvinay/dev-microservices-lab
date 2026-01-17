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
public class FieldDefinitionDto {

    private UUID id;
    private UUID spaceId;
    private String type;
    private String label;
    private String dataType;
    private String uiJson;
    private String validationJson;
    private String status;
    private Long createdAt;
    private Long updatedAt;
    private String createdBy;
    private String updatedBy;

    // getters and setters
}