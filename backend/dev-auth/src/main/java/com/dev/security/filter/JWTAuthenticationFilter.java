package com.dev.security.filter;

import com.dev.exception.AuthenticationException;
import com.dev.security.details.CustomAuthToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import static com.dev.security.SecurityConstants.*;

//@Component
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = request.getParameter(USERNAME);
        String password = request.getParameter(PASSWORD);
        String organization = request.getHeader(ORGANIZATION);
        CustomAuthToken authToken = null;
        if(StringUtils.hasText(username) && StringUtils.hasText(password) && StringUtils.hasText(organization)) {
            authToken = new CustomAuthToken(organization, username, password);
        }

        if(authToken == null) {
            authToken = getAuthTokenFromBasicAuth(request);
        }

        if(authToken == null) throw new AuthenticationException("Invalid Authentication request.");

        try {
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authentication successful for: {}", authentication.getPrincipal());
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Authentication failed: ", e);
            resetAuthenticationAfterRequest();
            handleAuthenticationFailure(response, e);
//            response.getWriter().write(e.getLocalizedMessage());
        } finally {
            // Ensures security context is always cleared after request
            resetAuthenticationAfterRequest();
        }
    }

    private void resetAuthenticationAfterRequest() {
        SecurityContextHolder.clearContext();
    }

    private void handleAuthenticationFailure(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
//        String json = new ObjectMapper().writeValueAsString(Map.of(
//                "error", "Unauthorized",
//                "message", e.getMessage()
//        ));
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + e.getMessage() + "\"}");
        response.getWriter().flush();
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
        String organization = request.getHeader(ORGANIZATION);

        return new CustomAuthToken(organization, username, password);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return !path.equals("/api/auth/login");  // Skip JWT processing for endpoint other that /login
    }
}
