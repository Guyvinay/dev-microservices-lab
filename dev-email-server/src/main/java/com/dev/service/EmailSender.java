package com.dev.service;

import com.dev.dto.email.EmailSendEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;

    public void send(EmailSendEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(event.getTo());
            helper.setFrom(event.getFrom());
            helper.setSubject(event.getSubject());
            helper.setText(event.getBody(), event.isHtml());

            if (event.getReplyTo() != null) {
                helper.setReplyTo(event.getReplyTo());
            }
            mailSender.send(message);
            log.info("Email sent to: {}", event.getTo());

        } catch (Exception ex) {
            throw new RuntimeException("SMTP send failed", ex);
        }
    }
}
