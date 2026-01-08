package com.dev.grpc.config;

import com.dev.grpc.interceptor.GrpcClientInterceptor;
import com.dev.grpc.interceptor.GrpcServerInterceptor;
import com.dev.logging.interceptors.grpc.GrpcMdcClientInterceptor;
import com.dev.logging.interceptors.grpc.GrpcMdcServerInterceptor;
import com.dev.provider.JwtTokenProviderManager;
import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.client.interceptor.GlobalClientInterceptorConfigurer;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GrpcConfiguration implements GlobalServerInterceptorConfigurer, GlobalClientInterceptorConfigurer {

    @Autowired
    private JwtTokenProviderManager jwtTokenProviderManager;

    @Override
    public void configureClientInterceptors(List<ClientInterceptor> interceptors) {
        interceptors.add(new GrpcClientInterceptor(jwtTokenProviderManager));
        interceptors.add(new GrpcMdcClientInterceptor());
    }

    @Override
    public void configureServerInterceptors(List<ServerInterceptor> interceptors) {
        interceptors.add(new GrpcServerInterceptor());
        interceptors.add(new GrpcMdcServerInterceptor());
    }
}


