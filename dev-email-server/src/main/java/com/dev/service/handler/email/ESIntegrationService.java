package com.dev.service.handler.email;

import com.dev.dto.email.EmailSendEvent;
import org.springframework.stereotype.Service;

@Service
public class ESIntegrationService {

    public void updateSuccessEvent(EmailSendEvent event) {

    }

    public void updateFailedEvent(EmailSendEvent event, String errorMessage) {

    }
}
