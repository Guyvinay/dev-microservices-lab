package com.dev.grpc;

import com.dev.common.dto.document.Document;
import com.dev.service.ElasticService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.List;

@GrpcService
public class DocumentService extends DocumentServiceGrpc.DocumentServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ElasticService elasticService;

    @Override
    public void getAllDocuments(Empty request, StreamObserver<DocumentsResponse> responseObserver) {
        List<Document> documents =  elasticService.getAllDocumentsFromElastic();
        List<com.dev.grpc.Document> protoDocs = new ArrayList<>();
        documents.forEach(doc-> {
            protoDocs.add(convertToProtoDocument(doc));
        });
        DocumentsResponse.Builder docs = DocumentsResponse.newBuilder();
        docs.addAllDocuments(protoDocs);
        docs.setCount(documents.size());
        responseObserver.onNext(docs.build());
        responseObserver.onCompleted();
    }
    private com.dev.grpc.Document convertToProtoDocument(Document document) {
        com.dev.grpc.Document.Builder protoDoc ;
        try {
            String jsonDocument = objectMapper.writeValueAsString(document);
            log.info("document {}", jsonDocument);
            protoDoc  = com.dev.grpc.Document.newBuilder();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return protoDoc.build();
    }

}
