package com.dev.grpc;

import com.dev.Gender;
import com.dev.User;
import com.dev.UserDetail;
import com.dev.UserServiceGrpc;
import com.dev.common.dto.document.Document;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GrpcClientServiceImpl {

    @GrpcClient("user_service_grpc")
    private UserServiceGrpc.UserServiceBlockingStub stub;

    @GrpcClient("user_service_grpc")
    private DocumentServiceGrpc.DocumentServiceBlockingStub documentServiceBlockingStub;

    public void getGrpcResponse() {
        User user = stub.createUser(UserDetail.newBuilder()
                                .setAge(24)
                                .setEmail("v@gmail.com")
                                .setName("Vinay Kumar Singh")
                                .setGender(Gender.MALE)
                                .build());
        log.info("user info get from grpc {}", user);
    }

    public List<Document> getAllDocuments() {
        DocumentsResponse documentsResponse = documentServiceBlockingStub.getAllDocuments(Empty.newBuilder().build());
        log.info("documents {}", documentsResponse);
return null;
    }

}
