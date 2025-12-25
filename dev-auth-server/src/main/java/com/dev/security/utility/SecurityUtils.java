package com.dev.security.utility;

import com.dev.security.dto.JwtTokenDto;
import com.dev.dto.UserProfileDetailsDto;
import com.dev.security.details.UserBaseInfo;
import com.dev.security.dto.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class SecurityUtils {

    public static JwtTokenDto createJwtTokeDtoFromModel(UserProfileDetailsDto userProfile, int expiredIn) {
        return createToken(mapToUserBaseInfo(userProfile), TokenType.ACCESS, expiredIn);
    }

    public static UserBaseInfo mapToUserBaseInfo(UserProfileDetailsDto userProfile) {
        return UserBaseInfo.builder()
                .id(userProfile.getUserId())
                .email(userProfile.getEmail())
                .name(userProfile.getName())
                .orgId(userProfile.getOrgId().toString())
                .tenantId(userProfile.getTenantId())
                .roleIds(userProfile.getRoleIds())
                .isActive(userProfile.getIsActive())
                .build();
    }

    /**
     * Creates a JwtTokenDto with dynamic expiration time.
     *
     * @param userBaseInfo user information to embed
     * @param expiryMinutes token validity in minutes
     * @return JwtTokenDto
     */
    public static JwtTokenDto createToken(
            UserBaseInfo userBaseInfo,
            TokenType tokenType,
            long expiryMinutes
    ) {
        if (userBaseInfo == null) {
            throw new IllegalArgumentException("UserBaseInfo must not be null");
        }
        if (expiryMinutes <= 0) {
            throw new IllegalArgumentException("Expiry minutes must be greater than zero");
        }

        long now = System.currentTimeMillis();
        long expiresAt = now + Duration.ofMinutes(expiryMinutes).toMillis();

        return JwtTokenDto.builder()
                .jwtId(UUID.randomUUID())
                .tokenType(tokenType)
                .userBaseInfo(userBaseInfo)
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();
    }
}
