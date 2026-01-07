package com.dev.library.logging.constant;

import com.dev.library.logging.MDCKeys;
import com.dev.security.dto.AccessJwtToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class MDCLoggingUtility {

    public static final String TENANT_ID = "tenantId";
    public static final String USER_ID = "userId";
    public static final String TRACE_ID = "traceId";

    public static void appendVariablesToMDC(AccessJwtToken accessJwtToken, HttpServletRequest request) {
        if(accessJwtToken != null && accessJwtToken.getUserBaseInfo() != null) {
            MDC.put(TENANT_ID, accessJwtToken.getUserBaseInfo().getTenantId());
            MDC.put(USER_ID, ObjectUtils.isNotEmpty(accessJwtToken.getUserBaseInfo().getId()) ? accessJwtToken.getUserBaseInfo().getId().toString() : accessJwtToken.getUserBaseInfo().getEmail());
        }

        String traceId = Optional.ofNullable(request.getHeader(TRACE_ID))
                .filter(StringUtils::isNoneBlank)
                .orElseGet(()-> {
                    String generated = UUID.randomUUID().toString();
                    log.debug("Generated new traceId={}", generated);
                    return generated;
                });

        MDC.put(TRACE_ID, traceId);

        if (accessJwtToken != null && accessJwtToken.getUserBaseInfo() != null) {
            putIfPresent(MDCKeys.TENANT_ID, accessJwtToken.getUserBaseInfo().getTenantId());
            putIfPresent(
                    MDCKeys.USER_ID,
                    Optional.ofNullable(accessJwtToken.getUserBaseInfo().getId())
                            .map(UUID::toString)
                            .orElse(accessJwtToken.getUserBaseInfo().getEmail())
            );
        }
    }

    public static void clear() {
        MDC.remove(MDCKeys.TRACE_ID);
        MDC.remove(MDCKeys.USER_ID);
        MDC.remove(MDCKeys.TENANT_ID);
    }

    private static void putIfPresent(String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            MDC.put(key, value);
        }
    }

    public static void removeVariablesFromMDCContext() {
        MDC.remove(TRACE_ID);
        MDC.remove(USER_ID);
        MDC.remove(TENANT_ID);
    }

    public static HttpRequest addMDCVariablesToHeaders(HttpRequest request) {
        HttpHeaders headers = request.getHeaders();

        headers.put(TRACE_ID, Arrays.asList(MDC.get(TRACE_ID)));
        headers.put(USER_ID, Arrays.asList(MDC.get(USER_ID)));
        headers.put(TENANT_ID, Arrays.asList(MDC.get(TENANT_ID)));

        return request;
    }
}
