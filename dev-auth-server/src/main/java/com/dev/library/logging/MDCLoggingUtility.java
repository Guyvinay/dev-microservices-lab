package com.dev.library.logging;

import com.dev.security.dto.JwtToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import io.grpc.Metadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class MDCLoggingUtility {

    public static void appendVariablesToMDC(JwtToken accessJwtToken, HttpServletRequest request) {

        String traceId = Optional.ofNullable(request.getHeader(MDCKeys.TRACE_ID))
                .filter(StringUtils::isNoneBlank)
                .orElseGet(() -> {
                    String generated = UUID.randomUUID().toString();
                    log.info("Generated new traceId={}", generated);
                    return generated;
                });

        MDC.put(MDCKeys.TRACE_ID, traceId);

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

    public static void populateMdcFromGrpcMetadata(Metadata headers) {

        String traceId = Optional
                .ofNullable(headers.get(MDCKeys.TRACE_ID_KEY))
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> {
                    String generated = UUID.randomUUID().toString();
                    log.info("Generated gRPC traceId={}", generated);
                    return generated;
                });

        putIfPresent(MDCKeys.TRACE_ID, traceId);
        putIfPresent(MDCKeys.TENANT_ID, headers.get(MDCKeys.TENANT_ID_KEY));
        putIfPresent(MDCKeys.USER_ID, headers.get(MDCKeys.USER_ID_KEY));
    }

    private static void putIfPresent(String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            MDC.put(key, value);
        }
    }

    public static void removeVariablesFromMDCContext() {
        MDC.remove(MDCKeys.TRACE_ID);
        MDC.remove(MDCKeys.USER_ID);
        MDC.remove(MDCKeys.TENANT_ID);
    }

    public static HttpRequest addMDCVariablesToHttpHeaders(HttpRequest request) {
        return new HttpRequestWrapper(request) {

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.addAll(super.getHeaders());
                addHeader(headers, MDCKeys.HEADER_TRACE_ID, MDC.get(MDCKeys.TRACE_ID));
                addHeader(headers, MDCKeys.HEADER_TENANT_ID, MDC.get(MDCKeys.TRACE_ID));
                addHeader(headers, MDCKeys.HEADER_USER_ID, MDC.get(MDCKeys.TRACE_ID));
                return headers;
            }
        };
    }

    private static void addHeader(HttpHeaders headers, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            headers.add(key, value);
        }
    }
}
