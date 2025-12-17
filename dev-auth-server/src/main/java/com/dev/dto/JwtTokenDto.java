package com.dev.dto;

import com.dev.security.details.UserBaseInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenDto {

    private UserBaseInfo userBaseInfo;
    private long createdAt;
    private long expiresAt;

}


