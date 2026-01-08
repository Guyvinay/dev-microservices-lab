package com.dev.security.dto;

import com.dev.security.details.UserBaseInfo;

import java.util.UUID;

public interface JwtToken {
    UUID getJwtId();
    TokenType getTokenType();
    long getCreatedAt();
    long getExpiresAt();
    void setJwtId(UUID jwtId);
    UserBaseInfo getUserBaseInfo();
}
