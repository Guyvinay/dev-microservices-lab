package com.dev.service.impl;


import com.dev.dto.email.EmailDocument;
import com.dev.library.elastic.service.EmailElasticSyncService;
import com.dev.library.rabbitmq.dto.RmqEvent;
import com.dev.library.rabbitmq.publisher.RabbitMqEventPublisher;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncEmailSendService {

    private final JavaMailSender mailSender;
    private final EmailElasticSyncService emailElasticSyncService;
    private final TemplateEngine templateEngine;
    private final RabbitMqEventPublisher tenantPublisher;

    @Async("threadPoolTaskExecutor")
    public void sendEmail(EmailDocument emailDocument) throws IOException {
        String threadName = Thread.currentThread().getName();

        long startTime = Instant.now().toEpochMilli();
        log.info("Preparing to send email to [{}] on thread [{}]", emailDocument.getEmailTo(), threadName);
        try {
            // ============================================================
            // 1️Prepare message
            // ============================================================
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            final String to = emailDocument.getEmailTo();
            final String from = emailDocument.getEmailFrom();
            final String subject = emailDocument.getSubject();

            messageHelper.setTo(to);
            messageHelper.setFrom(from);
            messageHelper.setSubject(subject);
            messageHelper.setText(emailDocument.getEmailTemplate(), emailDocument.isHtml());

            if(CollectionUtils.isNotEmpty(emailDocument.getAttachmentNames())) {
                for (String attachmentName: emailDocument.getAttachmentNames()) {
                    Resource resource = new ClassPathResource("data/" + attachmentName);

                    File file = resource.getFile();

                    if(file.exists()) {
                        messageHelper.addAttachment(attachmentName, file);
                    } else {
                        log.error("Attachment [{}] not found for recipient [{}]", attachmentName, to);
                        throw new RuntimeException("Attachment " + attachmentName + " not found for recipient " + to);
                    }
                }
            }

            // ============================================================
            // 3️Send email
            // ============================================================
            mailSender.send(message);

            // ============================================================
            // 4️Update EmailDocument metadata
            // ============================================================

            long endTime = Instant.now().toEpochMilli();
            long latency = endTime - startTime;

            emailDocument.setStatus("SUCCESS");
            emailDocument.setErrorMessage(null);
            emailDocument.setEmailSentTimes(emailDocument.getEmailSentTimes() + 1);
            emailDocument.setDeliveryTimeMs(latency);
            emailDocument.setLastUpdatedAt(Instant.now().toEpochMilli());
            emailDocument.setLastSentAt(Instant.now().toEpochMilli());
            emailDocument.setThreadName(threadName);
            log.info("Email successfully sent to [{}] ({} ms)", emailDocument.getEmailTo(), latency);

        } catch (MessagingException | MailException e) {
            // ============================================================
            // 5️Handle failure gracefully
            // ============================================================
            emailDocument.setErrorMessage(e.getMessage());
            emailDocument.setStatus("FAILED");
            emailDocument.setRetryCount(emailDocument.getRetryCount() + 1);
            emailDocument.setLastUpdatedAt(Instant.now().toEpochMilli());
            log.error("Failed to send email to [{}]. Error: {}", emailDocument.getEmailTo(), e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            emailDocument.setStatus("ERROR");
            emailDocument.setErrorMessage("Unexpected error: " + e.getMessage());
            emailDocument.setLastUpdatedAt(Instant.now().toEpochMilli());

            log.error("Unexpected error while sending email to [{}]: {}", emailDocument.getEmailTo(), e.getMessage(), e);
        }  finally {
            // ============================================================
            // 6️Persist final state to Elasticsearch
            // ============================================================
            try {
                indexEmailDocument(emailDocument);  // performs upsert in Elasticsearch
                log.info("Email document synced to Elasticsearch for [{}] (status: {})",
                        emailDocument.getEmailTo(), emailDocument.getStatus());
            } catch (Exception ex) {
                log.error("Failed to sync emailDocument for [{}] to Elasticsearch: {}",
                        emailDocument.getEmailTo(), ex.getMessage(), ex);
            }
        }
    }

    private void indexEmailDocument(EmailDocument emailDocument) {

        tenantPublisher.publish(
                RmqEvent.builder()
                    .routingKey("email.integration.q")
                    .exchange("integration.exchange")
                    .payload(emailDocument)
                .build());
    }

    @Async("threadPoolTaskExecutor")
    public void sendPasswordResetEmail(String to, String resetLink, String name, String token) throws MessagingException {

        // =============================================
        // 1️ Prepare email template variables
        // =============================================
        Map<String, String> templateVars = new HashMap<>();
        templateVars.put("resetLink", resetLink);
        templateVars.put("email", to);
        templateVars.put("name", name);
        templateVars.put("token", token);
        String htmlBody = prepareEmailTemplate(templateVars);


        // =============================================
        // 2️ Build email message
        // =============================================
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setFrom("mrsinghvinay563@gmail.com");
        helper.setSubject("Password reset request");
        helper.setText(htmlBody, true);    // true = HTML
        mailSender.send(message);
    }

    private String prepareEmailTemplate(Map<String, String> templateVariables) {
        Context context = new Context();
        for (Map.Entry<String, String> entry: templateVariables.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        return templateEngine.process("password-reset-email.html", context);
    }

    private void putIfPresent(MessageProperties props, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            props.setHeader(key, value);
        }
    }
}
