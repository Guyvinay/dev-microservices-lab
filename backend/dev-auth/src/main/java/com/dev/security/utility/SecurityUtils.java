package com.dev.security.utility;

import com.dev.dto.JwtTokenDto;
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

    public JwtTokenDto createJwtTokeDtoFromDTO(UserProfileResponseDTO userProfile, int expiredIn) {
        ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneOffset.UTC);
        Date createdDate = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        Date expiaryDate = Date.from(zdt.plusMinutes(expiredIn).toInstant());

        return new JwtTokenDto(
                userProfile.getId(),
                "org",
                userProfile.getName(),
                userProfile.getEmail(),
                "tenantId",
                createdDate,
                expiaryDate,
                List.of("123456", "234567", "345678", "56789", "67890")
        );
    }

    public JwtTokenDto createJwtTokeDtoFromModel(UserProfileModel userProfile, int expiredIn) {
        ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneOffset.UTC);
        Date createdDate = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        Date expiaryDate = Date.from(zdt.plusMinutes(expiredIn).toInstant());

        return new JwtTokenDto(
                userProfile.getId(),
                "org",
                userProfile.getName(),
                userProfile.getEmail(),
                "tenantId",
                createdDate,
                expiaryDate,
                List.of("123456", "234567", "345678", "56789", "67890")
        );
    }

    public JWTRefreshTokenDto createRefreshJwtTokeDto(UserProfileResponseDTO userProfile, int expiredIn) {
        ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneOffset.UTC);
        Date createdDate = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        Date expiaryDate = Date.from(zdt.plusMinutes(expiredIn).toInstant());
        return new JWTRefreshTokenDto(
                userProfile.getId(),
                "org",
                userProfile.getName(),
                userProfile.getEmail(),
                "tenant",
                createdDate,
                expiaryDate,
                List.of("123456", "234567", "345678", "56789", "67890")
        );
    }

}
