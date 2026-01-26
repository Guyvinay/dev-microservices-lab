package com.dev.service;

import com.dev.dto.email.EmailSendEvent;
import org.springframework.stereotype.Service;


@Service
public class EmailStatusPublisher {
    public void publishSuccess(EmailSendEvent event, long latency) {

    }

    public void publishFailure(EmailSendEvent event, String message, long latency) {

    }
}
