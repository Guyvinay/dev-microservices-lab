package com.dev.dto;

import java.util.UUID;

public interface JwtToken {
    UUID getJwtId();
    TokenType getTokenType();
    long getCreatedAt();
    long getExpiresAt();
    void setJwtId(UUID jwtId);
    UserBaseInfo getUserBaseInfo();
}
