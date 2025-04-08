package com.dev.auth.service.impl;

import com.dev.auth.dto.JwtTokenDto;
import com.dev.auth.dto.UserProfileResponseDTO;
import com.dev.auth.security.dto.JWTRefreshTokenDto;
import com.dev.auth.security.provider.JwtTokenProviderManager;
import com.dev.auth.service.AuthService;
import com.dev.auth.service.UserProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.authentication.AuthenticationManager;
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

import static com.dev.auth.security.SecurityConstants.JWT_REFRESH_TOKEN;
import static com.dev.auth.security.SecurityConstants.JWT_TOKEN;

@Service
public class AuthServiceImpl implements AuthService {


    private final AuthenticationManager authenticationManager;
    private final JwtTokenProviderManager jwtTokenProviderManager;
    private final UserProfileService userProfileService;


    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProviderManager jwtTokenProviderManager, UserProfileService userProfileService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProviderManager = jwtTokenProviderManager;
        this.userProfileService = userProfileService;
    }

    /**
     * @return
     */
    @Override
    public Map<String, String> login() throws JsonProcessingException, JOSEException {
//        Authentication authentication = new CustomAuthToken(loginRequestDTO.getOrgId(), loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
//        Authentication authenticated = authenticationManager.authenticate(authentication);
//        SecurityContextHolder.getContext().setAuthentication(authenticated);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getPrincipal().toString();
        int jwtExpiredIn = 200;
        int refreshExpiredIn = 1000;
        Map<String, String> tokensMap = new HashMap<>();

        UserProfileResponseDTO userProfile = userProfileService.getUserByEmail(username);
        JwtTokenDto jwtTokenDto = createJwtTokeDto(userProfile, jwtExpiredIn);
        JWTRefreshTokenDto jwtRefreshTokenDto = createRefreshJwtTokeDto(userProfile, refreshExpiredIn);
        tokensMap.put(JWT_TOKEN, jwtTokenProviderManager.createJwtToken( new ObjectMapper().writeValueAsString(jwtTokenDto), jwtExpiredIn));
        tokensMap.put(JWT_REFRESH_TOKEN, jwtTokenProviderManager.createJwtToken( new ObjectMapper().writeValueAsString(jwtRefreshTokenDto), refreshExpiredIn));

        return tokensMap;
    }

    private JwtTokenDto createJwtTokeDto(UserProfileResponseDTO userProfile, int expiredIn) {
        ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneOffset.UTC);
        Date createdDate = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        Date expiaryDate = Date.from(zdt.plusMinutes(expiredIn).toInstant());

        return new JwtTokenDto(
                userProfile.getId(),
                "org",
                userProfile.getName(),
                userProfile.getEmail(),
                "123456",
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
