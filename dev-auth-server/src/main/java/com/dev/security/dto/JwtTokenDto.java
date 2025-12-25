package com.dev.security.dto;

import com.dev.security.details.UserBaseInfo;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenDto {

    private UUID jwtId;
    private TokenType tokenType;
    private UserBaseInfo userBaseInfo;
    private long createdAt;
    private long expiresAt;

}


