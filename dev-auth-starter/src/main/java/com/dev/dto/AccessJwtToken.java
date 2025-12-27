package com.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessJwtToken implements JwtToken {

    private UUID jwtId;
    private TokenType tokenType;
    private UserBaseInfo userBaseInfo;
    private long createdAt;
    private long expiresAt;

}
