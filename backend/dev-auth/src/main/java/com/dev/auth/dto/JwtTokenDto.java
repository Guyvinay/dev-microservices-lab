package com.dev.auth.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenDto {

    private UUID userId;
    private String username;
    private String org;
    private String firstName;
    private String lastName;
    private String email;
    private String tenantId;
    private List<String> roles;

}


