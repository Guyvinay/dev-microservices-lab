package com.dev.security.utility;

import com.dev.dto.JwtTokenDto;
import com.dev.dto.UserProfileDetailsDto;
import com.dev.dto.UserProfileResponseDTO;
import com.dev.entity.UserProfileModel;
import com.dev.security.details.UserBaseInfo;
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
        UserBaseInfo userBaseInfo = UserBaseInfo.builder()
                .id(userProfile.getUserId())
                .build();

        return new JwtTokenDto(
                userBaseInfo,
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );
    }
}
