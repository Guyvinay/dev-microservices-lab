package com.dev;

import com.dev.exception.DivisionByZeroException;
import com.dev.grpc.document.DocumentServiceGrpc;
import com.dev.grpc.document.DocumentsResponse;
import com.dev.grpc.document.Empty;
import com.dev.rmq.wrapper.RabbitTemplateWrapper;
import com.dev.service.TestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DevTakeawayApplicationTests {

    @InjectMocks
    private TestService testService;

    @Mock
    private RabbitTemplateWrapper rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    @GrpcClient("user_service_grpc")
    private DocumentServiceGrpc.DocumentServiceBlockingStub documentServiceBlockingStub;


    @DisplayName("pushToQueue -> test common service message push to queue")
    @Test
    void pushToQueue() throws JsonProcessingException {
        // Arrange
        String queueName = "testQueue";
        Object data = new Object();
        String message = "testMessage";

        // Mock methods
        when(objectMapper.writeValueAsString(any())).thenReturn(message);

        // Act
        testService.pushToQueue(queueName, data);

        // Assert
        verify(rabbitTemplate).convertAndSend(eq(queueName), eq(message+"gh"));
    }

    @DisplayName("Test Dividing by Zero ")
    @Test
    void testDivide_throwsDivisionByZeroException() {
        // Arrange
        double numerator = 10;
        double denominator1 = 0;
        double denominator2 = 10;

        // Act and Assert
        assertThrows(DivisionByZeroException.class, () -> {
            testService.divide(numerator, denominator1);
        });
        assertThrows(DivisionByZeroException.class, () -> {
            testService.divide(numerator, denominator2);
        });
    }

    @DisplayName("Test Getting All Documents.")
    @Test
    void testGetAllDocuments() {

        when(documentServiceBlockingStub.getAllDocuments(Empty.newBuilder().build())).thenReturn(DocumentsResponse.newBuilder().build());
    }

}
