package com.dev.controller;

import com.dev.grpc.GrpcClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/grpc")
public class GrpcController {

    @Autowired
    private GrpcClientServiceImpl grpcClientService;

    @GetMapping
    public void sendGrpcRequest() {
        grpcClientService.getGrpcResponse();
    }
}
