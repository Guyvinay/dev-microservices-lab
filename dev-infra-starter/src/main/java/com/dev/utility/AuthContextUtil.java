

package com.dev.utility;

import com.dev.dto.JwtTokenDto;
import com.dev.exception.AuthenticationException;
import com.dev.grpc.constant.GRPCConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

public class AuthContextUtil {

    /* =========================
     * Jwt Token from gRPC context if found otherwise
     * From SecurityContextHolder.
     * ========================= */
    public static JwtTokenDto getJwtToken() {
        // gRPC context (only present during gRPC calls)
        JwtTokenDto grpcJwt = GRPCConstant.JWT_CONTEXT.get();
        if (grpcJwt != null) {
            return grpcJwt;
        }

        // Spring Security context (REST calls)
        return getJwtFromSecurityContext();
    }

    public static UUID getUserId() {
        JwtTokenDto jwt = getJwtToken();
        UUID userId = jwt.getUserBaseInfo().getId();

        if (userId == null) {
            throw new AuthenticationException("User ID not found in JWT token");
        }
        return userId;
    }

    public static String getTenantId() {
        JwtTokenDto jwt = getJwtToken();
        String tenantId = jwt.getUserBaseInfo().getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new AuthenticationException("Tenant ID not found in JWT token");
        }
        return tenantId;
    }

    public static String getOrgId() {
        JwtTokenDto jwt = getJwtToken();
        String orgId = jwt.getUserBaseInfo().getOrgId();

        if (orgId == null || orgId.isBlank()) {
            throw new AuthenticationException("Org ID not found in JWT token");
        }
        return orgId;
    }

    public static List<String> getRoles() {
        JwtTokenDto jwt = getJwtToken();
        return jwt.getUserBaseInfo().getRoleIds();
    }

    public static String resolveTenantIdOrNull() {
        try {
            return getTenantId();
        } catch (Exception ex) {
            return null;
        }
    }

    public static String resolveTenantIdOrDefault(String defaultTenant) {
        String tenantId = resolveTenantIdOrNull();
        return StringUtils.isNotBlank(tenantId) ? tenantId : defaultTenant;
    }

    private static JwtTokenDto getJwtFromSecurityContext() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AuthenticationException("No authentication found in SecurityContext");
        }

        Object details = authentication.getDetails();
        if (!(details instanceof JwtTokenDto jwtTokenDto)) {
            throw new AuthenticationException(
                    "Authentication details do not contain JwtTokenDto"
            );
        }

        return jwtTokenDto;
    }
}