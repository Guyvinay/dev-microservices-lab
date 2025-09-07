package com.dev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationTenantDTO {
    private String tenantId;

    @NotNull(message = "Organization ID is required")
    private UUID orgId;

    @NotBlank(message = "Tenant name is required")
    private String tenantName;

    private boolean tenantActive;
}
