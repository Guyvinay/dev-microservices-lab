package com.dev.grpc;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
//import net.devh.boot.grpc.client.inject.GrpcClient;
//import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Service
public class GrpcServiceClient{

//    @GrpcClient("GrpcClientImpl")
//    private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    public void getUser() {
        UserEntityRequest request = UserEntityRequest.newBuilder().setName("Vinay").setAge(20).setEmail("v@gmail.com").build();
//        UserEntityResponse response = userServiceBlockingStub.getUserById(request);
//        System.out.println(response);
    }
}
