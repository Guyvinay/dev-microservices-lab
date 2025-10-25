package com.dev.bulk.email.service;

import com.dev.bulk.email.dto.EmailDocument;
import com.dev.bulk.email.dto.EmailRequest;
import com.dev.elastic.client.EsRestHighLevelClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EsRestHighLevelClient esRestHighLevelClient;
    private final ObjectMapper objectMapper;
    private static final int DUPLICATE_DAYS = 30;


    @Async("threadPoolTaskExecutor")
    public void sendEmail(EmailRequest request, File resumeFile) throws MessagingException {
        log.info("Preparing email template for: {}, thread: {}", request.getEmailTo(), Thread.currentThread().getName());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.getEmailTo());
            helper.setSubject(request.getSubject());
            helper.setFrom(request.getEmailFrom());

            Map<String, String> templateVariables = new HashMap<>();
            templateVariables.put("name", request.getName());
            templateVariables.put("company", request.getCompany());
            String htmlContent = prepareEmailTemplate(templateVariables);

            helper.setText(htmlContent, true);

            helper.addAttachment("Vinay_Java_Backend_Developer.pdf", resumeFile);
            log.info("Sending email to: {}, company: {}", request.getEmailTo(), request.getCompany());
            mailSender.send(message);
            log.info("Email sent to: {}", request.getEmailTo());
        } catch (MessagingException | MailException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("Email sent in finally: {}", request.getEmailTo());
        }
    }

    private String prepareEmailTemplate(Map<String, String> templateVariables) {
        Context context = new Context();
        for (Map.Entry<String, String> entry: templateVariables.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        return templateEngine.process("email-template.html", context);
    }

    public Map<String, EmailDocument> processAndSyncEmails(String rawInput) throws IOException {
        Map<String, EmailDocument> emailDocuments = prepareEmailDocumentsFromRawInput(rawInput);
        log.info("emailDocuments prepared to sync to elastic: {}", emailDocuments.size());
        bulkIndexEmails(emailDocuments);

        return emailDocuments;
    }

    private void bulkIndexEmails(Map<String, EmailDocument> emailDocuments) throws IOException {
        if (emailDocuments.isEmpty()) return;
        log.info("email syncing to elastic start");

        BulkRequest bulkRequest = new BulkRequest();
        String index = _index();

        for (Map.Entry<String, EmailDocument> entry: emailDocuments.entrySet()) {
            String docId = entry.getKey();
            String jsonValue = objectMapper.writeValueAsString(entry.getValue());

            bulkRequest.add(
                    new IndexRequest(index)
                            .id(docId)
                            .source(jsonValue,  XContentType.JSON)
            );
        }

        BulkResponse bulkResponse = esRestHighLevelClient.bulkIndexDocument(bulkRequest);
        if (bulkResponse.hasFailures()) {
            log.warn("Bulk insert completed with some failures: {}", bulkResponse.buildFailureMessage());
        } else {
            log.info("Bulk insert successful: {} emails indexed", emailDocuments.size());
        }
    }

    private String _index() {
        return "email_index";
    }

    private Map<String, EmailDocument> prepareEmailDocumentsFromRawInput(String rawInput) {
        return Arrays.stream(rawInput.split("\\r?\\n"))
                .skip(1)
                .map(this::prepareEmailDocument)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        EmailDocument::getEmailTo,
                        Function.identity(),
                        (existing, replacement)-> existing
                ));
    }

    private EmailDocument prepareEmailDocument(String emailRow) {
        String[] parts = emailRow.split(",");
        String emailTo = parts[1].trim().toLowerCase();
        if (parts.length >= 3 && !validateEmailFormat(emailTo)) {
            log.warn("Invalid line skipped: {}", emailRow);
            return null;
        }

        String name = parts[0].trim();
        String company = parts[2].trim();

        // --- Initialize document ---
        EmailDocument emailDocument = new EmailDocument();
        emailDocument.setRecipientName(name);
        emailDocument.setEmailTo(emailTo);
        emailDocument.setCompany(company);
        emailDocument.setEmailFrom("mrsinghvinay563@gmail.com");
        emailDocument.setSubject("Java Backend Developer Application For New Opportunities");

        // --- Template & content ---
        emailDocument.setHtml(true);
        emailDocument.setTemplateName("email-template.html");
        Map<String, String> templateVariables = new HashMap<>();
        templateVariables.put("name", name);
        templateVariables.put("company", company);
        emailDocument.setTemplateVariables(templateVariables);
        emailDocument.setEmailTemplate(prepareEmailTemplate(templateVariables));

        // --- Attachments ---
        emailDocument.setAttachmentNames(
                new ArrayList<>(List.of("Vinay_Singh_Java_Backend_Developer.pdf"))
        );
        emailDocument.setAttachmentSizeBytes(0L); // You can populate later if you measure actual file size

        // --- Status & tracking ---
        emailDocument.setStatus("READY");
        emailDocument.setValidEmail(validateEmailFormat(emailTo));
        emailDocument.setRetryCount(0);
        emailDocument.setEmailSentTimes(0);
        emailDocument.setLastUpdatedAt(Instant.now().toEpochMilli());
        emailDocument.setSentAt(0L); // Not sent yet
        emailDocument.setDeliveryTimeMs(0L);

        // --- System info ---
        emailDocument.setEnvironment("development");
        emailDocument.setSentBy("mrsinghvinay563@gmail.com");
        emailDocument.setThreadName(Thread.currentThread().getName());

        return emailDocument;
    }

    private boolean validateEmailFormat(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }
}
