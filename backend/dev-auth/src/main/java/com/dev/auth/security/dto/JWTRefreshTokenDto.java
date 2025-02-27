package com.dev.auth.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JWTRefreshTokenDto {

    private UUID userId;
    private String username;
    private String org;
    private String firstName;
    private String lastName;
    private String email;
    private String tenantId;
    private Date createdDate;
    private Date expiryDate;
    private List<String> roles;
}
