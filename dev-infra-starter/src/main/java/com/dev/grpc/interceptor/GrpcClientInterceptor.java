package com.dev.grpc.interceptor;

import com.dev.dto.JwtTokenDto;
import com.dev.provider.JwtTokenProviderManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import io.grpc.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.dev.grpc.constant.GRPCConstant.JWT_TOKEN;

public class GrpcClientInterceptor implements ClientInterceptor {

    private final JwtTokenProviderManager jwtTokenProviderManager;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public GrpcClientInterceptor(JwtTokenProviderManager jwtTokenProviderManager1) {
        this.jwtTokenProviderManager = jwtTokenProviderManager1;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                JwtTokenDto tokenDto = new JwtTokenDto();
                if(auth != null) {
                    tokenDto = (JwtTokenDto) auth.getDetails();
                }

                try {
                    String token = jwtTokenProviderManager.createJwtToken(OBJECT_MAPPER.writeValueAsString(tokenDto), 2);

                    // Custom header laga do yaha
                    headers.put(JWT_TOKEN, token);

                } catch (JOSEException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                super.start(responseListener, headers);
            }
        };
    }
}
