package com.dev.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserProfileDTO {

    @Schema(description = "The unique identifier of the user profile", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "name of the user", example = "John", required = true)
    private String name;

    @Schema(description = "Email address of the user", example = "john.doe@example.com", required = true)
    private String email;

}
