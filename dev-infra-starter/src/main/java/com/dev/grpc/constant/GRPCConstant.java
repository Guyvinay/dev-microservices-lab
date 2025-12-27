package com.dev.grpc.constant;

import com.dev.dto.JwtTokenDto;
import io.grpc.Context;
import io.grpc.Metadata;

public class GRPCConstant {
    public static final String GRPC_AUTH = "__dev-auth-server__";

    public static final Metadata.Key<String> AUTHORIZATION =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<JwtTokenDto> JWT_CONTEXT =
            Context.key("grpc-jwt-context");
}
