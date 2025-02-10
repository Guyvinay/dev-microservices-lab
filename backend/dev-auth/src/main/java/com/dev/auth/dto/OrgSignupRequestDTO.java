package com.dev.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgSignupRequestDTO {

    @NotBlank(message = "Organization name is required")
    private String organizationName;

    @NotBlank(message = "Organization email is required")
    @Email(message = "Invalid email format")
    private String organizationEmail;

    @NotNull(message = "Organization contact is required")
    private Long organizationContact;

    @NotBlank(message = "Admin full name is required")
    private String adminFirstName;

    @NotBlank(message = "Admin last name is required")
    private String adminLastName;

    @NotBlank(message = "Admin email is required")
    @Email(message = "Invalid email format")
    private String adminEmail;

    @NotBlank(message = "Admin username is required")
    private String adminUsername;

    @NotBlank(message = "Admin password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String adminPassword;

    @NotBlank(message = "Tenant name is required")
    private String tenantName;

}
