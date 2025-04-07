package com.dev.auth.grpc.interceptor;

import com.dev.auth.dto.JwtTokenDto;
import com.dev.auth.security.provider.JwtTokenProviderManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import static com.dev.auth.grpc.constants.GrpcConstants.PVT_JWT_TOKEN;
import static com.dev.auth.grpc.constants.GrpcConstants.PVT_JWT_TOKEN_CTX;

@Slf4j
public class GrpcServerInterceptor implements ServerInterceptor {

    private final JwtTokenProviderManager jwtTokenHelper;
    private ObjectMapper OM = new ObjectMapper();

    public GrpcServerInterceptor(JwtTokenProviderManager jwtTokenHelper) {
        this.jwtTokenHelper = jwtTokenHelper;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, Metadata requestHeaders, ServerCallHandler<ReqT, RespT> next) {

        log.debug("Private JWT Token {}", requestHeaders.get(PVT_JWT_TOKEN));
        log.debug("ccreateTokenall {}", call.getMethodDescriptor().getRequestMarshaller());
        try {
            call.setCompression("gzip");
            // Get payload from token
            String payload = jwtTokenHelper.getSubjectPayload(requestHeaders.get(PVT_JWT_TOKEN));

            // Validate payload is a Private JWT Token
            JwtTokenDto pvtToken = OM.readValue(payload, JwtTokenDto.class);

            // Set the Private JWT Token into GRPC context
            Context grpcCtx = Context.current().withValue(PVT_JWT_TOKEN_CTX, payload);

            // Chain
            return Contexts.interceptCall(grpcCtx, call, requestHeaders, next);
        } catch (Exception e) {
            log.error("GRPC authentication failure {}", e.getMessage());
            call.close(Status.UNAUTHENTICATED.withDescription(e.getMessage()), requestHeaders);
            return new ServerCall.Listener<ReqT>() {};
        }
    }

}
