package com.dev.grpc;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class GrpcServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void getUserById(UserEntityRequest request, StreamObserver<UserEntityResponse> responseObserver) {
        UserEntityResponse.Builder builder = UserEntityResponse.newBuilder();
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        System.out.println(request);
    }
}
