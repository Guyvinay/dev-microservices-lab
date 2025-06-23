package com.dev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRoleDTO {

    private Long roleId;

    @NotBlank(message = "Role name is required")
    @Size(max = 100, message = "Role name must be at most 100 characters")
    private String roleName;

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    private boolean isActive;

    private boolean adminFlag;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    private String createdBy;

    private String updatedBy;
}