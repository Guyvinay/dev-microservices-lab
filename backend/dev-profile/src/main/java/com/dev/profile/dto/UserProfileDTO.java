package com.dev.profile.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserProfileDTO {

    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<UserRoleDTO> roles;
}
