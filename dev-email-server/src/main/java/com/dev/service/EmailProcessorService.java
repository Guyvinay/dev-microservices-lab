package com.dev.service;

import com.dev.dto.email.EmailSendEvent;
import com.dev.dto.email.EmailSendResult;
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

        EmailResultHandler handler = handlerRegistry.getHandler(event.getCategory());

        EmailSendResult result = emailSender.send(event);

        EmailStatusEvent statusEvent;

        if (result.isSuccess()) {
            statusEvent = handler.buildSuccessEvent(event, result);
            try {
                handler.onSuccess(statusEvent);
            } catch (Exception ex) {
                log.error("Post-success handler failure: eventId={} {}", event.getEventId(), ex.getMessage(), ex);
            }
        } else {
            statusEvent = handler.buildFailureEvent(event, result);
            try {
                handler.onFailure(statusEvent);
            } catch (Exception ex) {
                log.error("Post-failure handler failure: eventId={} {}", event.getEventId(), ex.getMessage(), ex);
            }
        }
    }

}
