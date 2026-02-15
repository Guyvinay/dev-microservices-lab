package com.dev.service.handler.email;

import com.dev.dto.email.EmailSendEvent;
import com.dev.dto.email.EmailStatusEvent;
import com.dev.util.EmailStatusPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ESIntegrationService {

    private final EmailStatusPublisher statusPublisher;

    public void updateSuccessEvent(EmailStatusEvent event) {
        statusPublisher.publish(event);
    }

    public void updateFailedEvent(EmailStatusEvent event) {
        statusPublisher.publish(event);
    }
}
