package com.dev.grpc.interceptor;

import com.dev.dto.AccessJwtToken;
import com.dev.dto.TokenType;
import com.dev.exception.AuthenticationException;
import com.dev.grpc.constant.GRPCConstant;
import com.dev.provider.JwtTokenProviderManager;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class GrpcServerInterceptor implements ServerInterceptor {

    private final JwtTokenProviderManager jwtTokenHelper;

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
            AccessJwtToken accessJwtToken = jwtTokenHelper.getJwtTokenDTOFromToken(authHeader, TokenType.ACCESS);

            Context grpcCtx = Context.current().withValue(GRPCConstant.JWT_CONTEXT, accessJwtToken);

            // Chain
            return Contexts.interceptCall(grpcCtx, call, requestHeaders, next);
        } catch (AuthenticationException ex) {
            log.error("gRPC auth failed: {}", ex.getMessage());
            call.close(Status.UNAUTHENTICATED.withDescription(ex.getMessage()), new Metadata());
            return new ServerCall.Listener<>() {};
        } catch (Exception ex) {
            log.error("gRPC auth error", ex);
            call.close(Status.INTERNAL.withDescription("Authentication error"), new Metadata());
            return new ServerCall.Listener<>() {};
        }
    }
}
