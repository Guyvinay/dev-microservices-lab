package com.dev.rest;

import com.dev.grpc.GrpcClientImpl;
import com.dev.grpc.UserEntityRequest;
import com.dev.grpc.UserEntityResponse;
import com.dev.grpc.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rest")
public class GrpcController {

    @Autowired
    private GrpcClientImpl grpcClient;


    @GetMapping
    public void getGrpc(){
            grpcClient.getGrpc();

    }

}
