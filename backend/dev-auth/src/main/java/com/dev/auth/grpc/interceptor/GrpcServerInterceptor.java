package com.dev.auth.grpc.interceptor;

import com.dev.auth.dto.JwtTokenDto;
import com.dev.auth.security.provider.JwtTokenProviderManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import static com.dev.auth.grpc.constants.GrpcConstants.JWT_TOKEN;
import static com.dev.auth.grpc.constants.GrpcConstants.JWT_TOKEN_CTX;

@Slf4j
public class GrpcServerInterceptor implements ServerInterceptor {

    private final JwtTokenProviderManager jwtTokenHelper;

    public GrpcServerInterceptor(JwtTokenProviderManager jwtTokenHelper) {
        this.jwtTokenHelper = jwtTokenHelper;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, Metadata requestHeaders, ServerCallHandler<ReqT, RespT> next) {

        log.info("Private JWT Token {}", requestHeaders.get(JWT_TOKEN));
        log.info("ccreateTokenall {}", call.getMethodDescriptor().getRequestMarshaller());
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
