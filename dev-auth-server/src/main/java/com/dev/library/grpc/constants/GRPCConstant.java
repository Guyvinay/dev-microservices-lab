package com.dev.library.grpc.constants;

import com.dev.security.dto.AccessJwtToken;
import com.dev.security.dto.JwtToken;
import io.grpc.Context;
import io.grpc.Metadata;

public class GRPCConstant {

    private GRPCConstant() {}

    public static final Metadata.Key<String> AUTHORIZATION =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<JwtToken> JWT_CONTEXT =
            Context.key("grpc-jwt-context");
}
