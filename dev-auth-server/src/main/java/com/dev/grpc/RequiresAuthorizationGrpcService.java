package com.dev.grpc;

import com.dev.service.AuthorizationEvaluator;
import com.dev.utility.grpc.RequiresAuthorizationGrpc;
import com.dev.utility.grpc.RequiresRequest;
import com.dev.utility.grpc.RequiresResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RequiresAuthorizationGrpcService extends RequiresAuthorizationGrpc.RequiresAuthorizationImplBase {

    private final AuthorizationEvaluator authorizationEvaluator;

    @Override
    public void validateRequires(RequiresRequest request, StreamObserver<RequiresResponse> responseObserver) {
        log.info("gRPC Authorization request received");

        boolean allowed = authorizationEvaluator.isAllowed(request);

        responseObserver.onNext(RequiresResponse.newBuilder().setAllowed(allowed).build());
        responseObserver.onCompleted();
    }

}
