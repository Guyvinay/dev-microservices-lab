package com.dev.auth.service.impl;

import com.dev.auth.dto.JwtTokenDto;
import com.dev.auth.dto.LoginRequestDTO;
import com.dev.auth.dto.UserProfileResponseDTO;
import com.dev.auth.security.details.CustomAuthToken;
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

import java.util.List;

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
     * @param loginRequestDTO
     * @return
     */
    @Override
    public String login(LoginRequestDTO loginRequestDTO) throws JsonProcessingException, JOSEException {
        Authentication authentication = new CustomAuthToken(loginRequestDTO.getOrgId(), loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
        Authentication authenticated = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticated);

        String username = authenticated.getPrincipal().toString();

        UserProfileResponseDTO userProfile = userProfileService.getUserByUsername(loginRequestDTO.getUsername());

        JwtTokenDto jwtTokenDto = new JwtTokenDto(
                userProfile.getId(),
                userProfile.getUsername(),
                "org",
                userProfile.getFirstName(),
                userProfile.getLastName(),
                userProfile.getEmail(),
                "tenant id",
                List.of("123456", "234567", "345678", "56789", "67890")
        );

        return jwtTokenProviderManager.createJwtToken( new ObjectMapper().writeValueAsString(jwtTokenDto), 2);
    }

}
