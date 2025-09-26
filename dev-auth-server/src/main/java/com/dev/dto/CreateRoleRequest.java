package com.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {
    private String roleName;
    private boolean adminFlag;
    private String tenantId;
    private String description;
    private String createdBy;
}