package com.dev.auth.grpc.configurer;

import com.dev.auth.dto.JwtTokenDto;
import com.dev.auth.grpc.interceptor.GrpcServerInterceptor;
import com.dev.auth.security.provider.JwtTokenProviderManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.StubTransformer;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.concurrent.Executor;

import static com.dev.auth.grpc.constants.GrpcConstants.PVT_JWT_TOKEN;

@Slf4j
@Configuration
public class GRPCServerConfigurer implements GlobalServerInterceptorConfigurer {

    @Autowired
    private JwtTokenProviderManager jwtTokenProviderManager;

    /**
     * @param interceptors
     */
    @Override
    public void configureServerInterceptors(List<ServerInterceptor> interceptors) {
        interceptors.add(new GrpcServerInterceptor(jwtTokenProviderManager));
    }

    @Bean
    public StubTransformer call() {
        return (name, stub) -> {

            return stub.withCallCredentials(new CallCredentials() {

                @Override
                public void thisUsesUnstableApi() {
                }

                @Override
                public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
                    Metadata metadata = new Metadata();
                    try {
                        /**
                         * This method should always provide current thread context
                         */
                        Object principal = SecurityContextHolder.getContext().getAuthentication().getDetails();
                        log.debug("thread principal within stub transformer {}", principal);
                        JwtTokenDto pvtJwt = new JwtTokenDto();

                        metadata.put(PVT_JWT_TOKEN, jwtTokenProviderManager.createJwtToken(new ObjectMapper().writeValueAsString(pvtJwt), 1000));

                    } catch (Exception e) {
                        log.error("token exception", e);
                    } finally {
                        applier.apply(metadata);
                    }
                }
            });
        };
    }

}
