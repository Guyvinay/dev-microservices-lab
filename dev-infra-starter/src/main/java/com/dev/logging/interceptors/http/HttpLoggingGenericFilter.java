package com.dev.logging.interceptors.http;

import com.dev.dto.JwtToken;
import com.dev.logging.MDCKeys;
import com.dev.logging.MDCLoggingUtility;
import com.dev.utility.AuthContextUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HttpLoggingGenericFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            JwtToken jwtToken = AuthContextUtil.getJwtFromSecurityContextOrNull();
            MDCLoggingUtility.appendVariablesToMDC(jwtToken, request);
            response.addHeader(MDCKeys.HEADER_TRACE_ID, MDC.get(MDCKeys.TRACE_ID));
            filterChain.doFilter(request, response);
        } finally {
            MDCLoggingUtility.removeVariablesFromMDCContext();
        }
    }
}
