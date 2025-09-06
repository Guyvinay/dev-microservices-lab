package com.dev.grpc.client;

import com.dev.common.dto.RequiresResponseDTO;
import com.dev.utility.grpc.RequiresAuthorizationGrpc;
import com.dev.utility.grpc.RequiresRequest;
import com.dev.utility.grpc.RequiresResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import static com.dev.grpc.constant.GRPCConstant.GRPC_AUTH;

@Component
public class RequiresAuthorizationGrpcClient {

    @GrpcClient(GRPC_AUTH)
    private RequiresAuthorizationGrpc.RequiresAuthorizationBlockingStub authorizationBlockingStub;

    public RequiresResponseDTO requiresAuthorizationGrpcClient(String actions, String privileges) {

        RequiresRequest authorizationReq = RequiresRequest.newBuilder().addActions(actions).setPrivilege(privileges).build();
        RequiresResponse requiresResponse = authorizationBlockingStub.validateRequires(authorizationReq);


        return RequiresResponseDTO.builder().allowed(requiresResponse.getAllowed()).build();
    }

}
