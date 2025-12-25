package com.dev.security.filter;

import com.dev.exception.AuthenticationException;
import com.dev.security.details.CustomAuthToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import static com.dev.security.SecurityConstants.*;

@Component
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    private AuthenticationManager getAuthManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("/dev-auth-server/api/auth/login".equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = request.getParameter(USERNAME);
        String password = request.getParameter(PASSWORD);
        CustomAuthToken authToken = null;
        if(StringUtils.hasText(username) && StringUtils.hasText(password)) {
            authToken = new CustomAuthToken(username, password);
        }

        if(authToken == null) {
            authToken = getAuthTokenFromBasicAuth(request);
        }

        if(authToken == null) throw new AuthenticationException("Invalid Authentication request.");

        try {
            Authentication authentication = getAuthManager().authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authentication successful for: {}", authentication.getPrincipal());
        } catch (Exception e) {
            String errMsg = "Exception in Authentication filter " + e.getMessage();
            SecurityContextHolder.clearContext();
            throw new AuthenticationException(errMsg, e);
        }
        filterChain.doFilter(request, response);
    }

    private CustomAuthToken getAuthTokenFromBasicAuth(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(Objects.isNull(authorizationHeader)) return null;
        authorizationHeader = authorizationHeader.trim();
        if(!StringUtils.startsWithIgnoreCase(authorizationHeader, BASIC_AUTH)) return null;
        // Extract and decode credentials from Basic Auth header
        String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String decodedCredentials = new String(decodedBytes);

        // Split into username and password (format: "username:password")
        String[] credentials = decodedCredentials.split(":", 2);
        if (credentials.length != 2) {
            throw new AuthenticationException("Invalid Basic Authentication format");
        }

        String username = credentials[0];
        String password = credentials[1];

        return new CustomAuthToken(username, password);
    }

}
