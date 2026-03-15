package com.dev.grpc.server;

import com.dev.service.TableCreationService;
import com.dev.utility.grpc.form.CreateTableRequest;
import com.dev.utility.grpc.form.CreateTableResponse;
import com.dev.utility.grpc.form.RecordSchemaServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
@RequiredArgsConstructor
public class RecordSchemaGrpcService extends RecordSchemaServiceGrpc.RecordSchemaServiceImplBase {

    private final TableCreationService tableCreationService;

    @Override
    public void createDynamicTable(CreateTableRequest request, StreamObserver<CreateTableResponse> responseObserver) {

        try {

            tableCreationService.createTable(
                    request.getSpaceId(),
                    request.getFormId(),
                    request.getTableName(),
                    request.getFieldsList()
            );

            CreateTableResponse response =
                    CreateTableResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Table created successfully")
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception ex) {

            CreateTableResponse response =
                    CreateTableResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage(ex.getMessage())
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

    }



}
