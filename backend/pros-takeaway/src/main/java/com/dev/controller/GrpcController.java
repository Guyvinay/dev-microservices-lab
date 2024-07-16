package com.dev.controller;

import com.dev.grpc.GrpcServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/grpc")
public class GrpcController {

//    @Autowired
    private GrpcServiceClient grpcServiceClient;

    @GetMapping
    public void grpcCall() {
        grpcServiceClient.getUser();
    }

}
