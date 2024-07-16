package com.dev.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class GrpcClientImpl {


    @GrpcClient("GrpcClientImpl")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public void getGrpc(){
        UserEntityRequest.Builder builder = UserEntityRequest.newBuilder();
        builder.setEmail("v@gmail.com");
        builder.setName("vinay");
        builder.setAge(23);
        UserEntityResponse response =  userServiceStub.getUserById(builder.build());
        System.out.println(response);

    }
}
