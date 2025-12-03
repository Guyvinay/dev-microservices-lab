

package com.dev.utility;

import com.dev.dto.JwtTokenDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public class SecurityContextUtil {

    public static String getTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authentication found in security context");
        }

        Object details = authentication.getDetails();
        if (!(details instanceof JwtTokenDto jwtTokenDto)) {
            throw new IllegalStateException("Authentication details do not contain JWT token information");
        }

        String tenantId = jwtTokenDto.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant ID not found in JWT token");
        }

        return tenantId;
    }


    public static String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authentication found in security context");
        }

        Object details = authentication.getDetails();
        if (!(details instanceof JwtTokenDto jwtTokenDto)) {
            throw new IllegalStateException("Authentication details do not contain JWT token information");
        }

        String userId = jwtTokenDto.getUserId().toString();
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("User ID not found in JWT token");
        }

        return userId;
    }

    public static JwtTokenDto getJwtTokenDtoFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authentication found in security context");
        }

        Object details = authentication.getDetails();
        if (!(details instanceof JwtTokenDto jwtTokenDto)) {
            throw new IllegalStateException("Authentication details do not contain JWT token information");
        }
        return jwtTokenDto;
    }
}