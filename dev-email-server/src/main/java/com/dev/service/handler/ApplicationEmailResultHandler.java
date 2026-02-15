package com.dev.service.handler;

import com.dev.dto.email.*;
import com.dev.service.handler.email.ESIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public EmailStatusEvent buildSuccessEvent(EmailSendEvent event, EmailSendResult result) {
        Map<String, Object> updateFieldMap = new HashMap<>();
        updateFieldMap.put("status", "SUCCESS");
        updateFieldMap.put("lastUpdatedAt", System.currentTimeMillis());
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
