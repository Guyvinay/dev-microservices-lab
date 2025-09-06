package com.dev.grpc.interceptor;

import com.dev.security.provider.JwtTokenProviderManager;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import static com.dev.grpc.constants.GrpcConstants.JWT_TOKEN;
import static com.dev.grpc.constants.GrpcConstants.JWT_TOKEN_CTX;

@Slf4j
public class GrpcServerInterceptor implements ServerInterceptor {

    private final JwtTokenProviderManager jwtTokenHelper;

    public GrpcServerInterceptor(JwtTokenProviderManager jwtTokenHelper) {
        this.jwtTokenHelper = jwtTokenHelper;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, Metadata requestHeaders, ServerCallHandler<ReqT, RespT> next) {

        log.info("JWT Token {}", requestHeaders.get(JWT_TOKEN));

        try {
            call.setCompression("gzip");
            // Get payload from token
            String payload = jwtTokenHelper.getSubjectPayload(requestHeaders.get(JWT_TOKEN));

            // Set the Private JWT Token into GRPC context
            Context grpcCtx = Context.current().withValue(JWT_TOKEN_CTX, payload);

            // Chain
            return Contexts.interceptCall(grpcCtx, call, requestHeaders, next);
        } catch (Exception e) {
            log.error("GRPC authentication failure {}", e.getMessage());
            call.close(Status.UNAUTHENTICATED.withDescription(e.getMessage()), requestHeaders);
            return new ServerCall.Listener<ReqT>() {};
        }
    }

}
