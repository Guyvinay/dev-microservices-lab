package com.dev.oauth2.handler;

import com.dev.dto.JwtTokenDto;
import com.dev.oauth2.dto.CustomOAuth2User;
import com.dev.security.details.CustomAuthToken;
import com.dev.security.provider.JwtTokenProviderManager;
import com.dev.security.utility.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProviderManager jwtTokenProviderManager;
    private final ObjectMapper objectMapper;


    public OAuth2LoginSuccessHandler(JwtTokenProviderManager jwtTokenProviderManager, ObjectMapper objectMapper) {
        this.jwtTokenProviderManager = jwtTokenProviderManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        JwtTokenDto jwtTokenDto = customUser.getJwtTokenDto();
        CustomAuthToken customAuthToken = new CustomAuthToken(jwtTokenDto.getEmail(), null, customUser.getAuthorities());
        customAuthToken.setDetails(jwtTokenDto);
        SecurityContextHolder.getContext().setAuthentication(customAuthToken);
        String token;
        try {
            token = jwtTokenProviderManager.createJwtToken(objectMapper.writeValueAsString(jwtTokenDto), 2000000000);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        log.info("OAuth2 Authentication successful: {}", jwtTokenDto);

        // Redirect with JWT token
        response.setContentType("application/json");
//        response.getWriter().write("{\"token\": \"" + token + "\"}");
        response.getWriter().write(token);
    }
}
