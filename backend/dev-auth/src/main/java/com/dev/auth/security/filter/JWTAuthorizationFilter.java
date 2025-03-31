package com.dev.auth.security.filter;

import com.dev.auth.security.provider.JwtTokenProviderManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Component
@Slf4j
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    public static final RequestMatcher REQUESTMATCHER = new AntPathRequestMatcher("/signin", "POST");
    private final JwtTokenProviderManager jwtTokenProvider;

    public JWTAuthorizationFilter(JwtTokenProviderManager jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("JWTAuthenticationFilter invoked for: {}", request.getRequestURI());

        String token = jwtTokenProvider.resolveToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Authentication failed:", e);
            handleAuthenticationFailure(response, e);
            resetAuthenticationAfterRequest();
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
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + e.getMessage() + "\"}");
        response.getWriter().flush();
    }

    /**
     * Skips JWT authentication for specific endpoints like /login
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/api/auth/login");  // Skip JWT processing for login endpoint
    }
}
