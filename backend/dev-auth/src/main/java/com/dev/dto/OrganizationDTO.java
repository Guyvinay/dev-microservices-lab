package com.dev.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
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
public class OrganizationDTO {

    private UUID orgId;

    @NotBlank(message = "Organization name is required")
    private String orgName;

    @NotBlank(message = "Organization email is required")
    @Email(message = "Invalid email format")
    private String orgEmail;

    @NotNull(message = "Organization contact is required")
    @Min(value = 1000000000, message = "Invalid contact number")
    private Long orgContact;
}
