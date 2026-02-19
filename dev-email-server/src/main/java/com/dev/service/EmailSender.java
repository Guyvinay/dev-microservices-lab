package com.dev.service;

import com.dev.dto.email.AttachmentRef;
import com.dev.dto.email.EmailSendEvent;
import com.dev.dto.email.EmailSendResult;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;

    public EmailSendResult send(EmailSendEvent event) {
        validateEvent(event);
        long start = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(event.getTo());
            helper.setFrom(event.getFrom());

            if (StringUtils.isNotBlank(event.getReplyTo())) {
                helper.setReplyTo(event.getReplyTo());
            }

            if (StringUtils.isNotBlank(event.getSenderName())) {
                helper.setFrom(new InternetAddress(
                        event.getFrom(),
                        event.getSenderName()
                ));
            }

            if (CollectionUtils.isNotEmpty(event.getCc())) {
                helper.setCc(event.getCc().toArray(new String[0]));
            }

            if (CollectionUtils.isNotEmpty(event.getBcc())) {
                helper.setBcc(event.getBcc().toArray(new String[0]));
            }

            // ==============================
            // Attachments
            // ==============================
            attachFiles(helper, event.getAttachments());

            helper.setSubject(event.getSubject());
            helper.setText(event.getBody(), event.isHtml());

            if (event.getReplyTo() != null) {
                helper.setReplyTo(event.getReplyTo());
            }

//            mailSender.send(message);

            long latency = System.currentTimeMillis() - start;
            String smtpMessageId = message.getMessageID();

            log.info(
                    "Email sent successfully to={} category={} priority={} latencyMs={} thread={} messageId={}",
                    event.getTo(), event.getCategory(), event.getPriority(), latency, threadName, smtpMessageId
            );

            return EmailSendResult.builder()
                    .to(event.getTo())
                    .category(event.getCategory())
                    .priority(event.getPriority())
                    .success(true)
                    .deliveryTimeMs(latency)
                    .smtpMessageId(smtpMessageId)
                    .threadName(threadName)
                    .build();

        } catch (MailSendException | MessagingException ex) {
            return buildFailureResult(event, start, threadName, ex);
        } catch (Exception ex) {
            return buildFailureResult(event, start, threadName, ex);
        }
    }

    private void attachFiles(MimeMessageHelper helper, List<AttachmentRef> attachments) throws MessagingException, IOException {

        if (CollectionUtils.isEmpty(attachments)) return;
        for (AttachmentRef attachment : attachments) {
            if (attachment == null) continue;
            DataSource dataSource = resolveAttachmentDataSource(attachment);
            helper.addAttachment(attachment.getFileName(), dataSource);
        }
    }

    private DataSource resolveAttachmentDataSource(AttachmentRef attachment) throws IOException {

        Resource resource = new ClassPathResource("data/" + attachment.getFileName());
        File file = resource.getFile();
        if (!file.exists()) {
            throw new IllegalArgumentException("Attachment file not found: " + attachment.getFileName());
        }
        return new FileDataSource(file);
    }

    private void validateEvent(EmailSendEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("EmailSendEvent cannot be null");
        }

        if (StringUtils.isBlank(event.getTo())) {
            throw new IllegalArgumentException("Recipient (to) cannot be blank");
        }

        if (StringUtils.isBlank(event.getFrom())) {
            throw new IllegalArgumentException("Sender (from) cannot be blank");
        }

        if (StringUtils.isBlank(event.getSubject())) {
            throw new IllegalArgumentException("Subject cannot be blank");
        }

        if (event.getBody() == null) {
            throw new IllegalArgumentException("Email body cannot be null");
        }
    }

    private EmailSendResult buildFailureResult(
            EmailSendEvent event,
            long start,
            String threadName,
            Exception ex
    ) {

        long latency = System.currentTimeMillis() - start;

        log.error(
                "Email failed to={} category={} priority={} latencyMs={} thread={} reason={}", event.getTo(),
                event.getCategory(), event.getPriority(), latency, threadName, ex.getMessage(), ex
        );

        return EmailSendResult.builder()
                .to(event.getTo())
                .category(event.getCategory())
                .priority(event.getPriority())
                .success(false)
                .deliveryTimeMs(latency)
                .errorMessage(ex.getMessage())
                .threadName(threadName)
                .build();
    }

}
