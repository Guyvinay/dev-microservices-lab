package com.dev.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserProfileDTO {

//    private UUID id;
//    private String username;
//    private String email;
//    private String firstName;
//    private String lastName;
//    private Set<UserRoleDTO> roles;

    @Schema(description = "The unique identifier of the user profile", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "The unique username of the user profile", example = "jdoe")
    private String username;

    @Schema(description = "First name of the user", example = "John", required = true)
    private String firstName;

    @Schema(description = "Last name of the user", example = "Doe", required = true)
    private String lastName;

    @Schema(description = "Email address of the user", example = "john.doe@example.com", required = true)
    private String email;

    @Schema(description = "Roles assigned to the user", example = "[\"Admin\", \"User\"]")
    private Set<UserRoleDTO> roles;

}
