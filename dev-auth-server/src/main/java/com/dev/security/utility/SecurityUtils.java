package com.dev.security.utility;

import com.dev.dto.JwtTokenDto;
import com.dev.dto.UserProfileDetailsDto;
import com.dev.dto.UserProfileResponseDTO;
import com.dev.entity.UserProfileModel;
import com.dev.security.details.UserBaseInfo;
import com.dev.security.dto.JWTRefreshTokenDto;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Service
public class SecurityUtils {

    public JwtTokenDto createJwtTokeDtoFromModel(UserProfileDetailsDto userProfile, int expiredIn) {
        return createToken(mapToUserBaseInfo(userProfile), expiredIn);
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
                .userBaseInfo(userBaseInfo)
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();
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

}
