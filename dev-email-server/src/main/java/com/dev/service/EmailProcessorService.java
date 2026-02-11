package com.dev.service;

import com.dev.dto.email.EmailSendEvent;
import com.dev.service.handler.EmailResultHandler;
import com.dev.service.handler.EmailResultHandlerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailProcessorService {

    private final EmailSender emailSender;
    private final EmailResultHandlerRegistry handlerRegistry;

    public void sendEmail(EmailSendEvent event) {
        long startTime = System.currentTimeMillis();
        EmailResultHandler handler =
                handlerRegistry.getHandler(event.getCategory());
        try {

            emailSender.send(event);

            long latency = System.currentTimeMillis() - startTime;

            handler.onSuccess(event);

        } catch (Exception ex) {
            log.error("Email sending failed: to={} {}", event.getTo(), ex.getMessage());
            handler.onFailure(event, ex.getMessage());
        }

    }
}
