package com.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Long roleId;
    private String roleName;
    private boolean active;
    private boolean adminFlag;
    private String tenantId;
    private String description;
    private long createdAt;
    private long updatedAt;
    private String createdBy;
    private String updatedBy;
}
