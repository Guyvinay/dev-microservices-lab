package com.dev.service;

import com.dev.dto.email.EmailSendEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailProcessorService {
    private final EmailSender emailSender;
    private final EmailStatusPublisher statusPublisher;
    public void sendEmail(EmailSendEvent event) {
        long startTime = System.currentTimeMillis();
        try {

            emailSender.send(event);

            long latency = System.currentTimeMillis() - startTime;

            statusPublisher.publishSuccess(event, latency);


        } catch (Exception ex) {

            long latency = System.currentTimeMillis() - startTime;

            statusPublisher.publishFailure(event, ex.getMessage(), latency);

            log.error("Email sending failed: to={} {}", event.getTo(), ex.getMessage());

//            throw ex; // triggers RMQ retry
        }

    }
}
