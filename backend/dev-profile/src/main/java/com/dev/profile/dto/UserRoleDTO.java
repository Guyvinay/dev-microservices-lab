package com.dev.profile.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserRoleDTO {

    private UUID id;
    private String name;
    private Set<AuthorityDTO> authorities;
}
