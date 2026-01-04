package com.dev.rmqConsumer;

import com.dev.dto.email.EmailDocument;
import com.dev.rabbitmq.annotation.TenantRabbitListener;
import com.dev.service.EmailElasticService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;

@Slf4j
@TenantRabbitListener(
        value = "EmailServiceListener",
        queue = "email.integration.q",
        exchange = "integration.exchange",
        routingKey = "email.integration.q",
        type = "direct"
)
@RequiredArgsConstructor
public class EmailServiceListener implements MessageListener {

    private final EmailElasticService emailElasticService;
    private final ObjectMapper objectMapper;


    @Override
    public void onMessage(Message message) {
        log.info("message received");
        try {
            indexEmailDocument(objectMapper.readValue(message.getBody(), new TypeReference<EmailDocument>() {}));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void indexEmailDocument(EmailDocument emailDocument) throws IOException {
        emailElasticService.indexEmail(emailDocument);
    }
}
