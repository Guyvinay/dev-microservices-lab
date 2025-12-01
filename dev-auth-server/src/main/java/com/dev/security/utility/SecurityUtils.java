package com.dev.security.utility;

import com.dev.dto.JwtTokenDto;
import com.dev.dto.UserProfileDetailsDto;
import com.dev.dto.UserProfileResponseDTO;
import com.dev.entity.UserProfileModel;
import com.dev.security.dto.JWTRefreshTokenDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Service
public class SecurityUtils {

    public JwtTokenDto createJwtTokeDtoFromModel(UserProfileDetailsDto userProfile, int expiredIn) {
        ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneOffset.UTC);
        Date createdDate = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        Date expiaryDate = Date.from(zdt.plusMinutes(expiredIn).toInstant());

        return new JwtTokenDto(
                userProfile.getUserId(),
                String.valueOf(userProfile.getOrgId()),
                userProfile.getName(),
                userProfile.getEmail(),
                userProfile.getTenantId(),
                createdDate,
                expiaryDate,
                userProfile.getRoleIds()
        );
    }

}
