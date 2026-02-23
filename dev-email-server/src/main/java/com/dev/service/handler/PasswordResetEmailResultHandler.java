package com.dev.service.handler;

import com.dev.dto.email.EmailCategory;
import com.dev.dto.email.EmailSendEvent;
import com.dev.dto.email.EmailSendResult;
import com.dev.dto.email.EmailStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PasswordResetEmailResultHandler implements EmailResultHandler {
    @Override
    public EmailCategory getCategory() {
        return EmailCategory.PASSWORD_RESET;
    }

    @Override
    public void onSuccess(EmailStatusEvent event) {
        log.info("onSuccess To be implemented");
    }

    @Override
    public void onFailure(EmailStatusEvent event) {
        log.info("onFailure To be implemented");
    }


    @Override
    public EmailStatusEvent buildSuccessEvent(EmailSendEvent event, EmailSendResult result) {
        Map<String, Object> updateFieldMap = new HashMap<>();
        updateFieldMap.put("status", "SUCCESS");
        updateFieldMap.put("errorMessage", "");
        updateFieldMap.put("deliveryTimeMs", result.getDeliveryTimeMs());
        updateFieldMap.put("threadName", result.getThreadName());
        return EmailStatusEvent.builder()
                .eventId(event.getEventId())
                .category(event.getCategory())
                .updateFieldMap(updateFieldMap)
                .build();
    }

    @Override
    public EmailStatusEvent buildFailureEvent(EmailSendEvent event, EmailSendResult result) {
        Map<String, Object> updateFieldMap = new HashMap<>();
        updateFieldMap.put("status", "FAILED");
        updateFieldMap.put("errorMessage", result.getErrorMessage());
        updateFieldMap.put("lastUpdatedAt", System.currentTimeMillis());
        updateFieldMap.put("threadName", result.getThreadName());
        return EmailStatusEvent.builder()
                .eventId(event.getEventId())
                .category(event.getCategory())
                .updateFieldMap(updateFieldMap)
                .build();
    }
}
