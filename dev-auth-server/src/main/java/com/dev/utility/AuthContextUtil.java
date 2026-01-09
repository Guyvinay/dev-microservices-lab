

package com.dev.utility;

import com.dev.exception.AuthenticationException;
import com.dev.library.grpc.constants.GRPCConstant;
import com.dev.security.details.UserBaseInfo;
import com.dev.security.dto.AccessJwtToken;
import com.dev.security.dto.JwtToken;
import com.dev.security.dto.ServiceJwtToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

public class AuthContextUtil {

    /* =========================
     * Jwt Token from gRPC context if found otherwise
     * From SecurityContextHolder.
     * ========================= */
    public static JwtToken getJwtToken() {
        // gRPC context (only present during gRPC calls)
        JwtToken grpcJwt = GRPCConstant.JWT_CONTEXT.get();
        if (grpcJwt != null) {
            return grpcJwt;
        }

        // Spring Security context (REST calls)
        return getJwtFromSecurityContext();
    }

    public static UUID getUserId() {
        JwtToken jwt = getJwtToken();
        UUID userId = jwt.getUserBaseInfo().getId();

        if (userId == null) {
            throw new AuthenticationException("User ID not found in JWT token");
        }
        return userId;
    }

    public static String getTenantId() {
        JwtToken jwt = getJwtToken();
        String tenantId = jwt.getUserBaseInfo().getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new AuthenticationException("Tenant ID not found in JWT token");
        }
        return tenantId;
    }

    public static String getOrgId() {
        JwtToken jwt = getJwtToken();
        String orgId = jwt.getUserBaseInfo().getOrgId();

        if (orgId == null || orgId.isBlank()) {
            throw new AuthenticationException("Org ID not found in JWT token");
        }
        return orgId;
    }

    public static List<String> getRoles() {
        JwtToken jwt = getJwtToken();
        return jwt.getUserBaseInfo().getRoleIds();
    }

    public static UserBaseInfo getUserBaseInfo() {
        JwtToken jwt = getJwtToken();
        return jwt.getUserBaseInfo();
    }

    /* =========================
     * Internal helpers
     * ========================= */

    private static JwtToken getJwtFromSecurityContext() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AuthenticationException("No authentication found in SecurityContext");
        }

        Object details = authentication.getDetails();
        if (details instanceof AccessJwtToken accessJwtToken) return accessJwtToken;
        if (details instanceof ServiceJwtToken serviceJwtToken) return serviceJwtToken;

        throw new AuthenticationException(
                "Authentication details do not contain JwtTokenDto"
        );
    }

    public static JwtToken getJwtFromSecurityContextOrNull() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) return null;

        Object details = authentication.getDetails();
        if (details instanceof AccessJwtToken accessJwtToken) return accessJwtToken;
        if (details instanceof ServiceJwtToken serviceJwtToken) return serviceJwtToken;
        return null;
    }
}