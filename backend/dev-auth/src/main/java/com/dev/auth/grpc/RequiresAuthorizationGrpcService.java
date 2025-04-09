package com.dev.auth.grpc;

import com.dev.utility.grpc.RequiresAuthorizationGrpc;
import com.dev.utility.grpc.RequiresRequest;
import com.dev.utility.grpc.RequiresResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class RequiresAuthorizationGrpcService extends RequiresAuthorizationGrpc.RequiresAuthorizationImplBase {

    @Override
    public void validateRequires(RequiresRequest request, StreamObserver<RequiresResponse> responseObserver) {
        RequiresResponse response = RequiresResponse.newBuilder().setAllowed(false).build();


        log.info("************************* :Called Grpc service: *************************");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
