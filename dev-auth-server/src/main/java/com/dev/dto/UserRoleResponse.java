package com.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleResponse {
    private UUID mappingId;
    private UUID userId;
    private Long roleId;
    private String roleName;
    private boolean adminFlag;
    private boolean defaultRole;
    private String tenantId;
}
