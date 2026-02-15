package com.dev.service.impl;

import com.dev.dto.email.EmailDocument;
import com.dev.utility.grpc.email.*;
import com.dev.utils.GrpcMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dev.utils.GRPCConstant.DEV_INTEGRATION;


@Slf4j
@Component
@RequiredArgsConstructor
public class EsEmailDocumentService {
    private static final int LOOKUP_BATCH_SIZE = 200;

    @GrpcClient(DEV_INTEGRATION)
    private EmailElasticServiceGrpc.EmailElasticServiceBlockingStub elasticServiceStub;

    private final AsyncEmailSendService asyncEmailSendService;

    // ==============================
    // ES LOOKUP
    // ==============================
    public Map<String, EmailDocument> fetchExistingDocuments(List<String> allEmails) {

        Map<String, EmailDocument> existingDocs = new HashMap<>();

        for (List<String> batch : partition(allEmails, LOOKUP_BATCH_SIZE)) {

            EmailLookupRequest request =
                    EmailLookupRequest.newBuilder()
                            .addAllEmailIds(batch)
                            .build();

            EmailLookupResponse response =
                    elasticServiceStub.getEmailDocumentsByEmailIds(request);

            response.getDocumentsList()
                    .forEach(doc ->
                            existingDocs.put(
                                    doc.getEmailTo(),
                                    GrpcMapper.fromProto(doc)
                            ));
        }

        log.info("Existing docs found: {}", existingDocs.size());
        return existingDocs;
    }

    // ==============================
    // BULK INDEX
    // ==============================

    public void bulkIndexNewDocuments(List<EmailDocument> newDocs) {

        if (newDocs.isEmpty()) return;

        List<com.dev.utility.grpc.email.EmailDocument> grpcDocs =
                newDocs.stream()
                        .map(GrpcMapper::toProto)
                        .toList();

        BulkIndexEmailRequest bulkRequest =
                BulkIndexEmailRequest.newBuilder()
                        .addAllDocuments(grpcDocs)
                        .build();

        BulkIndexEmailResponse response =
                elasticServiceStub.bulkIndexEmails(bulkRequest);

        if (!response.getFailedEmailIdsList().isEmpty()) {
            log.error("Bulk indexing failed for: {}",
                    response.getFailedEmailIdsList());
            throw new IllegalStateException("Bulk indexing failed");
        }

        log.info("Bulk indexed {} new documents",
                response.getIndexedCount());
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

}
