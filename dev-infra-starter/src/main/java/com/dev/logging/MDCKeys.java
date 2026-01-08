package com.dev.logging;

import io.grpc.Metadata;

public final class MDCKeys {

    private MDCKeys() {}

    public static final String TRACE_ID = "traceId";
    public static final String TENANT_ID = "tenantId";
    public static final String USER_ID = "userId";

    public static final String HEADER_TRACE_ID = "X-Trace-Id";
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";
    public static final String HEADER_USER_ID = "X-User-Id";

    public static final Metadata.Key<String> TRACE_ID_KEY =
            Metadata.Key.of(TRACE_ID, Metadata.ASCII_STRING_MARSHALLER);

    public static final Metadata.Key<String> TENANT_ID_KEY =
            Metadata.Key.of(TENANT_ID, Metadata.ASCII_STRING_MARSHALLER);

    public static final Metadata.Key<String> USER_ID_KEY =
            Metadata.Key.of(USER_ID, Metadata.ASCII_STRING_MARSHALLER);
}
