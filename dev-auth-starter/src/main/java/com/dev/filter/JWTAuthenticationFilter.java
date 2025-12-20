package com.dev.filter;

import com.dev.exception.AuthenticationException;
import com.dev.provider.JwtTokenProviderManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
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
import java.text.ParseException;

@Component
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    public static final RequestMatcher REQUESTMATCHER = new AntPathRequestMatcher("/signin", "POST");
    private final JwtTokenProviderManager jwtTokenProvider;

    public JWTAuthenticationFilter(JwtTokenProviderManager jwtTokenProvider) {
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
        if (token != null) {
            try {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JsonProcessingException | JOSEException | ParseException ex) {
                String errMsg = "Exception in Authentication filter " + ex.getMessage();
                SecurityContextHolder.clearContext();
                throw new AuthenticationException(errMsg, ex);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Skips JWT authentication for specific endpoints like /login
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/dev-auth-server/api/auth/login");  // Skip JWT processing for login endpoint
    }
}
