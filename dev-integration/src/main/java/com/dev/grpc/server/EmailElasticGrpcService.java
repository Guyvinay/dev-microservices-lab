package com.dev.grpc.server;

import com.dev.dto.email.EmailDocument;
import com.dev.service.EmailElasticService;
import com.dev.utility.grpc.email.*;
import com.dev.utils.GrpcMapper;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.io.IOException;
import java.util.ArrayList;
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
        List<EmailDocument> documents = null;
        try {
            documents = emailElasticService.getEmailDocumentFromEmailIds(emailIds);
        } catch (Exception e) {
            log.error("Exception while fetching documents: {}", e.getMessage(), e);
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

    @Override
    public void bulkIndexEmails(
            BulkIndexEmailRequest request,
            StreamObserver<BulkIndexEmailResponse> responseObserver) {

        List<com.dev.utility.grpc.email.EmailDocument> documents = request.getDocumentsList();
        log.info("Received bulk index request for {} emails", documents.size());

        List<String> failedEmailIds = new ArrayList<>();
        int indexedCount = 0;

        try {
            // Map proto EmailDocument -> internal EmailDocument model
            List<EmailDocument> internalDocs = documents.stream()
                    .map(GrpcMapper::fromProto)
                    .toList();

            // Call your existing bulk index method
            try {
                failedEmailIds = emailElasticService.bulkIndexEmail(internalDocs);
                indexedCount = internalDocs.size();
            } catch (Exception e) {
                log.error("Bulk index failed", e);
                // Fallback: mark all as failed
                failedEmailIds = internalDocs.stream()
                        .map(EmailDocument::getEmailTo)
                        .toList();
            }

        } catch (Exception e) {
            log.error("Error processing bulk index request", e);
        }

        BulkIndexEmailResponse response = BulkIndexEmailResponse.newBuilder()
                .setIndexedCount(indexedCount)
                .addAllFailedEmailIds(failedEmailIds)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
