package com.dev.grpc;

import com.dev.UserServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class GrpcServerImpl extends UserServiceGrpc.UserServiceImplBase {
    
}
