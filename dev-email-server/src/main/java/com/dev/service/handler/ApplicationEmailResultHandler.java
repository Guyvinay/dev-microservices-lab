package com.dev.service.handler;

import com.dev.dto.email.EmailCategory;
import com.dev.dto.email.EmailSendEvent;
import com.dev.dto.email.EmailStatusEvent;
import com.dev.service.handler.email.ESIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationEmailResultHandler implements EmailResultHandler {

    private final ESIntegrationService esIntegrationService;

    @Override
    public EmailCategory getCategory() {
        return EmailCategory.APPLICATION;
    }

    @Override
    public void onSuccess(EmailStatusEvent event) {
        log.info("APPLICATION email indexed in ES: {}", event.getEventId());
        esIntegrationService.updateSuccessEvent(event);
    }

    @Override
    public void onFailure(EmailStatusEvent event) {
        log.warn("APPLICATION email failure indexed in ES: {}", event.getEventId());
        esIntegrationService.updateFailedEvent(event);
    }

    @Override
    public EmailStatusEvent buildSuccessEvent(EmailSendEvent event) {
        Map<String, Object> updateFieldMap = new HashMap<>();
        updateFieldMap.put("status", "SUCCESS");
        updateFieldMap.put("lastUpdatedAt", System.currentTimeMillis());
        return EmailStatusEvent.builder()
                .eventId(event.getEventId())
                .category(event.getCategory())
                .updateFieldMap(updateFieldMap)
                .build();
    }

    @Override
    public EmailStatusEvent buildFailureEvent(EmailSendEvent event, String errorMessage) {
        Map<String, Object> updateFieldMap = new HashMap<>();
        updateFieldMap.put("status", "FAILED");
        updateFieldMap.put("errorMessage", errorMessage);
        updateFieldMap.put("lastUpdatedAt", System.currentTimeMillis());
        return EmailStatusEvent.builder()
                .eventId(event.getEventId())
                .category(event.getCategory())
                .updateFieldMap(updateFieldMap)
                .build();
    }
}
