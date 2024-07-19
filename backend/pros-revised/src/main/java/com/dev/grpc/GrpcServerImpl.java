package com.dev.grpc;

import com.dev.User;
import com.dev.UserDetail;
import com.dev.UserServiceGrpc;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class GrpcServerImpl extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void createUser(UserDetail request, StreamObserver<User> responseObserver) {

        log.info("request received {}", request);
        User user = User.newBuilder().setName("Vinay").build();
        responseObserver.onNext(user);
        responseObserver.onCompleted();
    }
}
