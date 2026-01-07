package com.dev.library.logging.interceptors.http;

import com.dev.security.dto.AccessJwtToken;
import com.dev.library.logging.constant.MDCLoggingUtility;
import com.dev.utility.AuthContextUtil;
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
            AccessJwtToken accessJwtToken = AuthContextUtil.getJwtFromSecurityContextOrNull();
            MDCLoggingUtility.appendVariablesToMDC(accessJwtToken, request);
            filterChain.doFilter(request, response);
        } finally {
            MDCLoggingUtility.removeVariablesFromMDCContext();
        }
    }
}
