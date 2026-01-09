package com.dev.grpc.interceptor;

import com.dev.dto.AccessJwtToken;
import com.dev.dto.ServiceJwtToken;
import com.dev.dto.TokenType;
import com.dev.dto.UserBaseInfo;
import com.dev.provider.JwtTokenProviderManager;
import com.dev.utility.AuthContextUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import io.grpc.*;

import static com.dev.grpc.constant.GRPCConstant.AUTHORIZATION;

public class GrpcClientInterceptor implements ClientInterceptor {

    private final JwtTokenProviderManager jwtTokenProviderManager;

    public GrpcClientInterceptor(JwtTokenProviderManager jwtTokenProviderManager1) {
        this.jwtTokenProviderManager = jwtTokenProviderManager1;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(channel.newCall(methodDescriptor, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                AccessJwtToken tokenDto = AuthContextUtil.getJwtToken();
                UserBaseInfo userBaseInfo = tokenDto.getUserBaseInfo();

                try {
                    ServiceJwtToken serviceJwtToken = ServiceJwtToken.builder()
                            .tokenType(TokenType.SERVICE)
                            .userBaseInfo(UserBaseInfo.builder()
                                    .tenantId(userBaseInfo.getTenantId())
                                    .build())
                            .build();

                    String token = jwtTokenProviderManager.createJwtToken(serviceJwtToken);

                    // Custom header
                    headers.put(AUTHORIZATION, token);

                } catch (JOSEException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                super.start(responseListener, headers);
            }
        };
    }
}
