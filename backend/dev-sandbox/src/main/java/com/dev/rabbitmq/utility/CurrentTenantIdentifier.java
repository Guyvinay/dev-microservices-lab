package com.dev.rabbitmq.utility;

import com.dev.dto.JWTToken;
import com.dev.dto.JwtTokenDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class CurrentTenantIdentifier {
    public static String getTenantId() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getDetails)
                .filter(details-> details instanceof JwtTokenDto)
                .map(JwtTokenDto.class::cast)
                .map(JwtTokenDto::getTenantId)
                .orElseGet(()->  {
                    return "public";

                    // maybe throw exception if required later
                });
    }
}
