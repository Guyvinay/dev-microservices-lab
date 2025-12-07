package com.dev.logging.interceptors.http;

import com.dev.dto.JwtTokenDto;
import com.dev.logging.constant.MDCLoggingUtility;
import com.dev.utility.SecurityContextUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HttpLoggingGenericFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            JwtTokenDto jwtTokenDto = SecurityContextUtil.getJwtTokenDtoFromContext();
            MDCLoggingUtility.appendVariablesToMDC(jwtTokenDto, request);
            filterChain.doFilter(request, response);
        } finally {
            MDCLoggingUtility.removeVariablesFromMDCContext();
        }
    }
}
