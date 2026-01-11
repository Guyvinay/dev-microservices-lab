package com.dev.grpc.server;

import com.dev.dto.email.EmailDocument;
import com.dev.service.EmailElasticService;
import com.dev.utility.grpc.email.EmailElasticServiceGrpc;
import com.dev.utility.grpc.email.EmailLookupRequest;
import com.dev.utility.grpc.email.EmailLookupResponse;
import com.dev.utils.GrpcMapper;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.io.IOException;
import java.util.List;

@Slf4j
@GrpcService
public class EmailElasticGrpcService extends EmailElasticServiceGrpc.EmailElasticServiceImplBase {
    private final EmailElasticService emailElasticService;

    public EmailElasticGrpcService(EmailElasticService emailElasticService) {
        this.emailElasticService = emailElasticService;
    }

    @Override
    public void getEmailDocumentsByEmailIds(
            EmailLookupRequest request,
            StreamObserver<EmailLookupResponse> responseObserver) {

        List<String> emailIds = request.getEmailIdsList();
        log.info("Received batch lookup for {} emails", emailIds.size());

        // IMPORTANT: Use ES multi-get or terms query
        List<EmailDocument> documents =
                null;
        try {
            documents = emailElasticService.getEmailDocumentFromEmailIds(emailIds);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EmailLookupResponse response = EmailLookupResponse.newBuilder()
                .addAllDocuments(
                        documents.stream()
                                .map(GrpcMapper::toProto)
                                .toList()
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
