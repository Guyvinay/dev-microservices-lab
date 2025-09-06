package com.dev.grpc;

import com.dev.grpc.profile.*;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class GrpcServerImpl extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void createUser(UserDetail request, StreamObserver<User> responseObserver) {
//        log.info("request received {}", request);
        User user = User.newBuilder()
                .setName("Vinay")
                .setAge(request.getAge())
                .setEmail(request.getEmail())
                .setDateOfBirth(request.getDateOfBirth())
                .build();
//        Users users = Users.newBuilder().setUser(1, request).setUser(2, request).build();
//        log.info("users {}", user);
        responseObserver.onNext(user);
        responseObserver.onCompleted();
    }
}
