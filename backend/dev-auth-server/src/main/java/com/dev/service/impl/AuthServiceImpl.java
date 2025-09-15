package com.dev.service.impl;

import com.dev.dto.JwtTokenDto;
import com.dev.dto.UserProfileResponseDTO;
import com.dev.security.details.CustomAuthToken;
import com.dev.security.dto.JWTRefreshTokenDto;
import com.dev.security.provider.JwtTokenProviderManager;
import com.dev.service.AuthService;
import com.dev.service.UserProfileService;
import com.dev.service.UserProfileTenantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dev.security.SecurityConstants.JWT_REFRESH_TOKEN;
import static com.dev.security.SecurityConstants.JWT_TOKEN;

@Service
public class AuthServiceImpl implements AuthService {


    private final JwtTokenProviderManager jwtTokenProviderManager;
    private final UserProfileService userProfileService;
    private final UserProfileTenantService userProfileTenantService;

    public AuthServiceImpl(JwtTokenProviderManager jwtTokenProviderManager, UserProfileService userProfileService, UserProfileTenantService userProfileTenantService) {
        this.jwtTokenProviderManager = jwtTokenProviderManager;
        this.userProfileService = userProfileService;
        this.userProfileTenantService = userProfileTenantService;
    }

    /**
     * @return
     */
    @Override
    public Map<String, String> login() throws JsonProcessingException, JOSEException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomAuthToken authToken = (CustomAuthToken) authentication;
        String username = authToken.getName();
        String orgId = authToken.getOrgId();
        String tenantId = authToken.getTenantId();

        int jwtExpiredIn = 2000000000;
        int refreshExpiredIn = 2000000000;
        Map<String, String> tokensMap = new HashMap<>();

        UserProfileResponseDTO userProfile = userProfileService.getUserByEmail(username);
        userProfileTenantService.getMappingsByUserId(userProfile.getId());
        JwtTokenDto jwtTokenDto = createJwtTokeDto(userProfile, orgId, tenantId, jwtExpiredIn);
        JWTRefreshTokenDto jwtRefreshTokenDto = createRefreshJwtTokeDto(userProfile, refreshExpiredIn);
        tokensMap.put(JWT_TOKEN, jwtTokenProviderManager.createJwtToken( new ObjectMapper().writeValueAsString(jwtTokenDto), jwtExpiredIn));
        tokensMap.put(JWT_REFRESH_TOKEN, jwtTokenProviderManager.createJwtToken( new ObjectMapper().writeValueAsString(jwtRefreshTokenDto), refreshExpiredIn));

        return tokensMap;
    }

    private JwtTokenDto createJwtTokeDto(UserProfileResponseDTO userProfile, String orgId, String tenantId, int expiredIn) {
        ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneOffset.UTC);
        Date createdDate = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        Date expiaryDate = Date.from(zdt.plusMinutes(expiredIn).toInstant());

        return new JwtTokenDto(
                userProfile.getId(),
                orgId,
                userProfile.getName(),
                userProfile.getEmail(),
                tenantId,
                createdDate,
                expiaryDate,
                List.of("123456", "234567", "345678", "56789", "67890")
        );
    }

    private JWTRefreshTokenDto createRefreshJwtTokeDto(UserProfileResponseDTO userProfile, int expiredIn) {
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
