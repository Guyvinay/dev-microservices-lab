package com.dev.library.grpc.configurer;

import com.dev.library.grpc.interceptor.GrpcClientInterceptor;
import com.dev.library.grpc.interceptor.GrpcServerInterceptor;
import com.dev.library.logging.interceptors.grpc.GrpcMdcClientInterceptor;
import com.dev.library.logging.interceptors.grpc.GrpcMdcServerInterceptor;
import com.dev.security.provider.JwtTokenProviderManager;
import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.interceptor.GlobalClientInterceptorConfigurer;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class GrpcConfiguration implements GlobalServerInterceptorConfigurer, GlobalClientInterceptorConfigurer {

    @Autowired
    private JwtTokenProviderManager jwtTokenProviderManager;

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        // You can implement your own reader, or use built-in ones like BasicGrpcAuthenticationReader
        return new BasicGrpcAuthenticationReader();
    }

    /**
     * @param interceptors
     */
    @Override
    public void configureServerInterceptors(List<ServerInterceptor> interceptors) {
        interceptors.add(new GrpcServerInterceptor(jwtTokenProviderManager));
        interceptors.add(new GrpcMdcServerInterceptor());
    }

    /**
     * @param interceptors
     */
    @Override
    public void configureClientInterceptors(List<ClientInterceptor> interceptors) {
        interceptors.add(new GrpcClientInterceptor(jwtTokenProviderManager));
        interceptors.add(new GrpcMdcClientInterceptor());
    }
}
