package com.dev.logging.utility;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestMDCContext {
    private final String traceId;
    private final String tenantId;
    private final String userId;
}
