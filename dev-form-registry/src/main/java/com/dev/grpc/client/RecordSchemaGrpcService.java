package com.dev.grpc.client;

import com.dev.utility.grpc.form.CreateTableRequest;
import com.dev.utility.grpc.form.CreateTableResponse;
import com.dev.utility.grpc.form.RecordSchemaServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import static com.dev.utils.GRPCConstant.DEV_RECORD_SERVER;

@Slf4j
@Component
public class RecordSchemaGrpcService {

    @GrpcClient(DEV_RECORD_SERVER)
    private RecordSchemaServiceGrpc.RecordSchemaServiceBlockingStub schemaServiceImplBase;

    public CreateTableResponse sendCreateTableRequestEvent(CreateTableRequest createTableRequest) {
        log.info("Sending CreateTableRequest for tableName={}", createTableRequest.getTableName());

        try {
            CreateTableResponse response = schemaServiceImplBase.createDynamicTable(createTableRequest);
            log.info("Received CreateTableResponse: success={}, message={}", response.getSuccess(), response.getMessage());
            return response;
        } catch (Exception ex) {
            log.error("gRPC call to create table failed for tableName={}", createTableRequest.getTableName(), ex);
            throw ex;
        }
    }

}
