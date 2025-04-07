package com.dev.auth.grpc.constants;

import io.grpc.Context;
import io.grpc.Metadata;

public class GrpcConstants {

    public static final Metadata.Key<String> PVT_JWT_TOKEN = Metadata.Key.of("PVT-JWT-TOKEN", Metadata.ASCII_STRING_MARSHALLER);
    public static final Context.Key<String> PVT_JWT_TOKEN_CTX = Context.key("PVT-JWT-TOKEN-CTX");

}
