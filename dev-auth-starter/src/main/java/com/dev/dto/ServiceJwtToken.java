package com.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceJwtToken implements JwtToken {

    private UUID jwtId;
    private TokenType tokenType;
    private UserBaseInfo userBaseInfo;
    private String serviceName;
    private List<String> scopes;
    private long createdAt;
    private long expiresAt;
}