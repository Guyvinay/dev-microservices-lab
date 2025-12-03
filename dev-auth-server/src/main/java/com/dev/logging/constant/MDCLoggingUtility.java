package com.dev.logging.constant;

import com.dev.dto.JwtTokenDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.UUID;

@Slf4j
public class MDCLoggingUtility {

    public static final String TENANT_ID = "tenantId";
    public static final String USER_ID = "userId";
    public static final String TRACE_ID = "traceId";

    public static void appendVariablesToMDC(JwtTokenDto jwtTokenDto, HttpServletRequest request) {
        MDC.put(TENANT_ID, jwtTokenDto.getTenantId());
        MDC.put(USER_ID, ObjectUtils.isNotEmpty(jwtTokenDto.getUserId()) ? jwtTokenDto.getUserId().toString() : jwtTokenDto.getEmail());

        String traceId = request.getHeader(TRACE_ID);

        if(StringUtils.isNotBlank(traceId)) {
            MDC.put(TRACE_ID, traceId);
        } else {
            traceId = UUID.randomUUID().toString();
            log.info("Found blank traceId from headers. Setting random traceId {}", traceId);
            MDC.put(TRACE_ID, traceId);
        }
    }

    public static void removeVariablesFromMDCContext() {
        MDC.remove(TRACE_ID);
        MDC.remove(USER_ID);
        MDC.remove(TENANT_ID);
    }

}
