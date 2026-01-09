package com.dev.library.grpc.interceptor;

import com.dev.exception.AuthenticationException;
import com.dev.library.grpc.constants.GRPCConstant;
import com.dev.security.dto.AccessJwtToken;
import com.dev.security.dto.JwtToken;
import com.dev.security.dto.TokenType;
import com.dev.security.provider.JwtTokenProviderManager;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class GrpcServerInterceptor implements ServerInterceptor {

    private final JwtTokenProviderManager jwtTokenHelper;

    public GrpcServerInterceptor(JwtTokenProviderManager jwtTokenHelper) {
        this.jwtTokenHelper = jwtTokenHelper;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, Metadata requestHeaders, ServerCallHandler<ReqT, RespT> next) {

        try {
            call.setCompression("gzip");
            String authHeader = requestHeaders.get(GRPCConstant.AUTHORIZATION);
            if (StringUtils.isBlank(authHeader)) {
                throw new AuthenticationException("Missing Authorization header");
            }
            log.info("GrpcServerInterceptor: JWT Token received from grpc request");
            // Get jwtTokenDto from token
            JwtToken accessJwtToken = jwtTokenHelper.getJwtTokenDTOFromToken(authHeader, TokenType.SERVICE);

            Context grpcCtx = Context.current().withValue(GRPCConstant.JWT_CONTEXT, accessJwtToken);

            // Chain
            return Contexts.interceptCall(grpcCtx, call, requestHeaders, next);
        } catch (AuthenticationException ex) {
            log.error("gRPC auth failed: {}", ex.getMessage(), ex);
            call.close(Status.UNAUTHENTICATED.withDescription(ex.getMessage()), new Metadata());
            return new ServerCall.Listener<>() {};
        } catch (Exception ex) {
            log.error("gRPC auth error: {}", ex.getMessage(), ex);
            call.close(Status.INTERNAL.withDescription("Authentication error"), new Metadata());
            return new ServerCall.Listener<>() {};
        }
    }

}
