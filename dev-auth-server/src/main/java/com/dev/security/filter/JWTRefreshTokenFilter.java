package com.dev.security.filter;

import com.dev.exception.AuthenticationException;
import com.dev.security.dto.TokenType;
import com.dev.security.provider.JwtTokenProviderManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JWTRefreshTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProviderManager jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("/dev-auth-server/api/auth/refresh".equals(request.getRequestURI()));
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String refreshToken = jwtTokenProvider.resolveToken(request);

        if (refreshToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Authentication auth = jwtTokenProvider.getAuthentication(refreshToken, TokenType.REFRESH);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        } catch (JsonProcessingException | JOSEException | ParseException e) {
            String errMessage = "Exception in authorization filter " + e.getMessage();
            logger.error(errMessage);
            SecurityContextHolder.clearContext();
            throw new AuthenticationException(errMessage, e);
        }
    }
}
