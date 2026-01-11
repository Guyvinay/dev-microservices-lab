package com.dev.library.grpc.interceptor;

import com.dev.security.dto.JwtToken;
import com.dev.security.provider.JwtTokenProviderManager;
import com.dev.utility.AuthContextUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import io.grpc.*;
import lombok.RequiredArgsConstructor;

import static com.dev.library.grpc.constants.GRPCConstant.AUTHORIZATION;

@RequiredArgsConstructor
public class GrpcClientInterceptor implements ClientInterceptor {
    private final JwtTokenProviderManager jwtTokenProviderManager;

    /**
     * @param methodDescriptor 
     * @param callOptions
     * @param channel
     * @param <ReqT>
     * @param <RespT>
     * @return
     */
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                JwtToken tokenDto = AuthContextUtil.getJwtToken();

                try {
                    String token = jwtTokenProviderManager.createJwtToken(tokenDto);

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
