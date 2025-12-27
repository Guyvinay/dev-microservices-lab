package com.dev.grpc;

import com.dev.security.details.UserBaseInfo;
import com.dev.security.dto.JwtTokenDto;
import com.dev.utility.AuthContextUtil;
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
        log.info("************************* :Called Grpc service: *************************");
        RequiresResponse response = RequiresResponse.newBuilder().setAllowed(true).build();
        JwtTokenDto jwtTokenDto = AuthContextUtil.getJwtToken();
        UserBaseInfo userBaseInfo = jwtTokenDto.getUserBaseInfo();
        log.info("User: {}, tenant: {}, roles: {}", userBaseInfo.getEmail(), userBaseInfo.getTenantId(), userBaseInfo.getRoleIds());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
