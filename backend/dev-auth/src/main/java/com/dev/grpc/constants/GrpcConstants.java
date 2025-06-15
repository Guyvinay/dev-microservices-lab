package com.dev.grpc.constants;

import io.grpc.Context;
import io.grpc.Metadata;

public class GrpcConstants {

    public static final Metadata.Key<String> JWT_TOKEN = Metadata.Key.of("WT-TOKEN", Metadata.ASCII_STRING_MARSHALLER);
    public static final Context.Key<String> JWT_TOKEN_CTX = Context.key("PVT-JWT-TOKEN-CTX");

}
