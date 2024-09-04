package com.dev.grpc;

import com.dev.common.annotations.LogExecutionTime;
import com.dev.common.dto.document.Document;
import com.dev.common.utility.GrpcUtils;
import com.dev.service.ElasticService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.dev.grpc.document.*;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class DocumentService extends DocumentServiceGrpc.DocumentServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ElasticService elasticService;

    @Autowired
    private GrpcUtils grpcUtils;

    @Override
    public void getDocumentsByQueries(DocumentSearchRequest request, StreamObserver<DocumentsResponse> responseObserver) {
        List<Document> documents =  elasticService.getDocumentsByQueries(request);
        List<com.dev.grpc.document.Document> protoDocs = new ArrayList<>();
        documents.forEach(doc-> {
//            protoDocs.add(convertToProtoDocument(doc));
            protoDocs.add(grpcUtils.convertToProto(doc, com.dev.grpc.document.Document.class));
        });
        log.info("documents {}", documents.size());
        DocumentsResponse.Builder docs = DocumentsResponse.newBuilder();
        docs.addAllDocuments(protoDocs);
        docs.setCount(documents.size());
        responseObserver.onNext(docs.build());
        responseObserver.onCompleted();
    }

    @Override
    @LogExecutionTime
    public void getAllDocuments(Empty request, StreamObserver<DocumentsResponse> responseObserver) {
        List<Document> documents =  elasticService.getAllDocumentsFromElastic();
        List<com.dev.grpc.document.Document> protoDocs = new ArrayList<>();
        documents.forEach(doc-> {
//            protoDocs.add(convertToProtoDocument(doc));
            protoDocs.add(grpcUtils.convertToProto(doc, com.dev.grpc.document.Document.class));
        });
        log.info("documents {}", documents.size());
        DocumentsResponse.Builder docs = DocumentsResponse.newBuilder();
        docs.addAllDocuments(protoDocs);
        docs.setCount(documents.size());
        responseObserver.onNext(docs.build());
        responseObserver.onCompleted();
    }
    private com.dev.grpc.document.Document convertToProtoDocument(Document document) {
        com.dev.grpc.document.Document.Builder protoDoc;
        try {
            String jsonDocument = objectMapper.writeValueAsString(document);
//            log.info("document {}", jsonDocument);
            protoDoc  = com.dev.grpc.document.Document.newBuilder();
            JsonFormat.parser().merge(jsonDocument, protoDoc);
        } catch (JsonProcessingException | InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        return protoDoc.build();
    }

}
