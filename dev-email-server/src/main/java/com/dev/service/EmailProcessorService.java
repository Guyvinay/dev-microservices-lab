package com.dev.service;

import com.dev.dto.email.EmailSendEvent;
import com.dev.dto.email.EmailStatusEvent;
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
        EmailResultHandler handler =
                handlerRegistry.getHandler(event.getCategory());
        try {

            emailSender.send(event);

            EmailStatusEvent statusEvent =
                    handler.buildSuccessEvent(event);

            handler.onSuccess(statusEvent);

        } catch (Exception ex) {
            log.error("Email sending failed: to={} {}", event.getTo(), ex.getMessage(), ex);
            EmailStatusEvent statusEvent =
                    handler.buildFailureEvent(event, ex.getMessage());
            handler.onFailure(statusEvent);
        }

    }
}
