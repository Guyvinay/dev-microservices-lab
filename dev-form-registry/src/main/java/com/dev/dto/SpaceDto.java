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
public class SpaceDto {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private Long createdAt;
    private Long updatedAt;
    private String createdBy;
    private String updatedBy;

    // getters and setters
}