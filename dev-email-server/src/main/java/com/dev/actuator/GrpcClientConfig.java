package com.dev.actuator;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ManagedChannel devAuthServerChannel() {
        return ManagedChannelBuilder
                .forAddress("localhost", 50054) // replace with your gRPC server host + port
                .usePlaintext()                // if not using TLS
                .build();
    }
}