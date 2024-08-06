package com.dev.common.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GrpcUtils {

    private static final Logger log = LoggerFactory.getLogger(GrpcUtils.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T, P extends Message> P convertToProto(T javaObject, Class<P> protoClass) {
        try {
            //convert Java Object into string
            String jsonDocument = objectMapper.writeValueAsString(javaObject);
            log.info("document {}", jsonDocument);

            // Get the builder from the proto class
            Message.Builder builder = (Message.Builder) protoClass.getMethod("newBuilder").invoke(null);

            // Parse the JSON into the proto builder
            JsonFormat.parser().merge(jsonDocument, builder);

            // Build the proto message
            return (P) builder.build();
        } catch (JsonProcessingException | InvalidProtocolBufferException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
