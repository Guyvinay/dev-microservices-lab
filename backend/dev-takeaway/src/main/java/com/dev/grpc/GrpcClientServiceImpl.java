package com.dev.grpc;

import com.dev.common.annotations.LogExecutionTime;
import com.dev.common.dto.document.DocumentSearchRequestDTO;
import com.dev.grpc.document.*;
import com.dev.grpc.profile.*;
import com.dev.common.dto.Profile;
import com.dev.common.dto.document.Document;
import com.dev.grpc.profile.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GrpcClientServiceImpl {

    @GrpcClient("user_service_grpc")
    private UserServiceGrpc.UserServiceBlockingStub stub;

    @GrpcClient("user_service_grpc")
    private DocumentServiceGrpc.DocumentServiceBlockingStub documentServiceBlockingStub;
//    @Autowired
//    private ObjectMapper objectMapper;
    public void getGrpcResponse() {
        ObjectMapper mapper = new ObjectMapper();
        User user = stub.createUser(UserDetail.newBuilder()
                                .setAge(24)
                                .setEmail("v@gmail.com")
                                .setName("Vinay Kumar Singh")
                                .setGender(Gender.MALE)
                                .setDateOfBirth("01-01-2001")
                                .build());
        try {
            String jsonFormat = JsonFormat.printer().print(user);
            log.info("user, {}", jsonFormat);
             Profile profile =  mapper.readValue(jsonFormat, Profile.class);
            log.info("user info get from grpc {}", profile);
        } catch (InvalidProtocolBufferException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @LogExecutionTime()
    public List<Document> getAllDocuments() {
        List<Document> documents = new ArrayList<>();
        DocumentsResponse documentsResponse = documentServiceBlockingStub.getAllDocuments(Empty.newBuilder().build());
        log.info("documents {}", documentsResponse.getCount());
        documentsResponse.getDocumentsList().forEach(doc-> {
            documents.add(convertProtoDocToJavaDoc(doc));
        });
        return documents;
    }

    public List<Document> getDocumentsByQueries(DocumentSearchRequestDTO documentSearchRequestDTO) {
        List<Document> documents = new ArrayList<>();
        DocumentSearchRequest.Builder builder =  DocumentSearchRequest.newBuilder();
        if(!StringUtils.isEmpty(documentSearchRequestDTO.getUserEmail())) {
            builder.setUserEmail(documentSearchRequestDTO.getUserEmail());
        }
        if(!StringUtils.isEmpty(documentSearchRequestDTO.getUserName())) {
            builder.setUserName(documentSearchRequestDTO.getUserName());
        }
        if(null!=documentSearchRequestDTO.getUserZipCode() ) {
            builder.setUserZipCode(documentSearchRequestDTO.getUserZipCode());
        }
        if (!StringUtils.isEmpty(documentSearchRequestDTO.getUserCity())) {
            builder.setUserCity(documentSearchRequestDTO.getUserCity());
        }
        if (!StringUtils.isEmpty(documentSearchRequestDTO.getUserState())) {
            builder.setUserState(documentSearchRequestDTO.getUserState());
        }
        DocumentsResponse documentsResponse = documentServiceBlockingStub.getDocumentsByQueries(builder.build());
        log.info("documents {}", documentsResponse.getCount());
        documentsResponse.getDocumentsList().forEach(doc-> {
            documents.add(convertProtoDocToJavaDoc(doc));
        });
        return documents;
    }

    private Document convertProtoDocToJavaDoc(com.dev.grpc.document.Document doc) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonFormat = JsonFormat.printer().print(doc);
            return mapper.readValue(jsonFormat, Document.class);
        } catch (InvalidProtocolBufferException e) {
            log.error("Error: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

   /* private Document convertMapToDocument(Map<String, Object> doc) {
        return objectMapper.convertValue(doc, Document.class);
    }
*/
}
