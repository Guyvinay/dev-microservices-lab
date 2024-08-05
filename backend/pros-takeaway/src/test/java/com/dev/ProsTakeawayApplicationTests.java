package com.dev;

import com.dev.exception.DivisionByZeroException;
import com.dev.rmq.wrapper.RabbitTemplateWrapper;
import com.dev.service.TestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProsTakeawayApplicationTests {

    @InjectMocks
    private TestService testService;

    @Mock
    private RabbitTemplateWrapper rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;


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

}
