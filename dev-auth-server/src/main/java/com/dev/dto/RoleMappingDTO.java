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
public class RoleMappingDTO {
    private UUID id;
    private UUID userId;
    private Long roleId;
    private Boolean defaultRole;
    private String tenantId;
}