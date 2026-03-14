package com.dev.grpc.server;

import com.dev.utility.grpc.form.CreateTableRequest;
import com.dev.utility.grpc.form.CreateTableResponse;
import com.dev.utility.grpc.form.RecordSchemaServiceGrpc;
import io.grpc.stub.StreamObserver;

public class RecordSchemaGrpcService extends RecordSchemaServiceGrpc.RecordSchemaServiceImplBase {


    public void createDynamicTable(CreateTableRequest request, StreamObserver<CreateTableResponse> responseObserver) {
    }



}
