package com.dev.auth.dto;

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
    @NotBlank(message = "Tenant ID is required")
    private UUID tenantId;

    @NotNull(message = "Organization ID is required")
    private UUID orgId;

    @NotBlank(message = "Tenant name is required")
    private String tenantName;

    private boolean tenantActive;
}
