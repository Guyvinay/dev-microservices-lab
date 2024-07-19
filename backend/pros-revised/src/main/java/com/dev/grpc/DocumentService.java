package com.dev.grpc;

import com.dev.common.dto.document.Document;
import com.dev.service.ElasticService;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DocumentService extends DocumentServiceGrpc.DocumentServiceImplBase {

    @Autowired
    private ElasticService elasticService;

    @Override
    public void getAllDocuments(Empty request, StreamObserver<DocumentsResponse> responseObserver) {
        List<Document> documents =  elasticService.getAllDocumentsFromElastic();
        DocumentsResponse.newBuilder();
        DocumentsResponse.Builder docs = DocumentsResponse.newBuilder();
        docs.addAllDocuments(documents);
        responseObserver.onNext(docs.build());
    }

}
