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
public class UserProfileTenantDTO {

    private UUID id;

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Organization ID is required")
    private UUID organizationId;
}
