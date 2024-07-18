package com.dev.grpc;

import com.dev.User;
import com.dev.UserDetail;
import com.dev.UserServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GrpcClientServiceImpl {

    @GrpcClient("user_service_grpc")
    private UserServiceGrpc.UserServiceBlockingStub stub;

    public void getGrpcResponse() {
        User user= stub.createUser(UserDetail.newBuilder()
                                .setAge(24)
                                .setEmail("v@gmail.com")
                                .setName("Vinay Kumar Singh")
                                .build());
        log.info("user info get from grpc {}", user);
    }

}
