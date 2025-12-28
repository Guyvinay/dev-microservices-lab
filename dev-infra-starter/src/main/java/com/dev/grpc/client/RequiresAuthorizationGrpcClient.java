package com.dev.grpc.client;

import com.dev.common.dto.RequiresResponseDTO;
import com.dev.dto.privilege.Action;
import com.dev.dto.privilege.Privilege;
import com.dev.utility.grpc.PrivilegeActions;
import com.dev.utility.grpc.RequiresAuthorizationGrpc;
import com.dev.utility.grpc.RequiresRequest;
import com.dev.utility.grpc.RequiresResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.dev.grpc.constant.GRPCConstant.GRPC_AUTH;

@Component
public class RequiresAuthorizationGrpcClient {

    @GrpcClient(GRPC_AUTH)
    private RequiresAuthorizationGrpc.RequiresAuthorizationBlockingStub authorizationBlockingStub;

    public RequiresResponseDTO requiresAuthorizationGrpcClient(
            Map<Privilege, Set<Action>> required
    ) {

        RequiresRequest.Builder requestBuilder = RequiresRequest.newBuilder();

        for (Map.Entry<Privilege, Set<Action>> entry : required.entrySet()) {

            PrivilegeActions privilegeActions = PrivilegeActions.newBuilder()
                    .setPrivilege(entry.getKey().name())
                    .addAllActions(
                            entry.getValue().stream()
                                    .map(Enum::name)
                                    .toList()
                    )
                    .build();

            requestBuilder.addRequired(privilegeActions);
        }

        RequiresRequest request = requestBuilder.build();

        RequiresResponse response =
                authorizationBlockingStub.validateRequires(request);

        return RequiresResponseDTO.builder()
                .allowed(response.getAllowed())
                .build();
    }
}
