package com.dev.grpc;

import com.dev.common.dto.document.DocumentResponseDTO;
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

    public DocumentResponseDTO getAllDocuments() {
        DocumentResponseDTO documentResponseDTO = new DocumentResponseDTO();
        List<Document> documents = new ArrayList<>();
        DocumentsResponse documentsResponse = documentServiceBlockingStub.getAllDocuments(Empty.newBuilder().build());
        log.info("documents {}", documentsResponse.getCount());
        documentsResponse.getDocumentsList().forEach(doc-> {
            documents.add(convertProtoDocToJavaDoc(doc));
        });
        documentResponseDTO.setDocuments(documents);
        documentResponseDTO.setDocumentCount(documentsResponse.getDocumentsCount());
        return documentResponseDTO;
    }

    public DocumentResponseDTO getDocumentsByQueries(DocumentSearchRequestDTO documentSearchRequestDTO) {
        DocumentResponseDTO documentResponseDTO = new DocumentResponseDTO();
        List<Document> documents = new ArrayList<>();
        DocumentSearchRequest.Builder builder =  DocumentSearchRequest.newBuilder();
        builder.setUserEmail(documentSearchRequestDTO.getUserEmail());
        DocumentsResponse documentsResponse = documentServiceBlockingStub.getDocumentsByQueries(builder.build());
        log.info("documents {}", documentsResponse.getCount());
        documentsResponse.getDocumentsList().forEach(doc-> {
            documents.add(convertProtoDocToJavaDoc(doc));
        });
        documentResponseDTO.setDocuments(documents);
        documentResponseDTO.setDocumentCount(documentsResponse.getDocumentsCount());
        return documentResponseDTO;
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
