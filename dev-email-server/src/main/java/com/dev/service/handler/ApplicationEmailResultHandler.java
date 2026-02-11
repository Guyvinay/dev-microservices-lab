package com.dev.service.handler;

import com.dev.dto.email.EmailCategory;
import com.dev.dto.email.EmailSendEvent;
import com.dev.service.handler.email.ESIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public void onSuccess(EmailSendEvent event) {
        esIntegrationService.updateSuccessEvent(event);
    }

    @Override
    public void onFailure(EmailSendEvent event, String errorMessage) {
        esIntegrationService.updateFailedEvent(event, errorMessage);
    }
}
