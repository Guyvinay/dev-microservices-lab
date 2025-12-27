package com.dev.grpc.constants;

import com.dev.security.dto.JwtTokenDto;
import io.grpc.Context;
import io.grpc.Metadata;

public class GRPCConstant {

    private GRPCConstant() {}

    public static final Metadata.Key<String> AUTHORIZATION =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<JwtTokenDto> JWT_CONTEXT =
            Context.key("grpc-jwt-context");
}
