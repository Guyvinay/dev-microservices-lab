package com.dev.service.impl;

import com.dev.dto.email.EmailDocument;
import com.dev.dto.email.EmailRequest;
import com.dev.library.elastic.service.EmailElasticSyncService;
import com.dev.utility.grpc.email.EmailElasticServiceGrpc;
import com.dev.utility.grpc.email.EmailLookupRequest;
import com.dev.utility.grpc.email.EmailLookupResponse;
import com.dev.utils.GrpcMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dev.utils.GRPCConstant.DEV_INTEGRATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailPrepareService {

    private final TemplateEngine templateEngine;
    private final EmailElasticSyncService emailElasticSyncService;
    private static final int HOURS_TO = 360;
    private final AsyncEmailSendService asyncEmailSendService;

    @GrpcClient(DEV_INTEGRATION)
    private EmailElasticServiceGrpc.EmailElasticServiceBlockingStub elasticServiceStub;

    public void sendEmailFromCSVFile(MultipartFile file) throws IOException {
        List<String> skippedEmails = new ArrayList<>();
        Map<String, EmailRequest> toSend = getEmailRequestsFromCSV(file);
        List<String> allEmails = new ArrayList<>(toSend.keySet());
        log.info("Total emails from CSV: {}", allEmails.size());
        Map<String, EmailDocument> existingDocs = new HashMap<>();

        for (List<String> batch : partition(allEmails, 100)) {
            EmailLookupRequest emailLookupRequest = EmailLookupRequest.newBuilder().addAllEmailIds(batch).build();
            EmailLookupResponse emailLookupResponse = elasticServiceStub.getEmailDocumentsByEmailIds(emailLookupRequest);

            emailLookupResponse.getDocumentsList()
                    .forEach(doc -> existingDocs.put(doc.getEmailTo(), GrpcMapper.fromProto(doc)));

        }

        log.info("Existing doc found: {}", existingDocs.size());

        for (String emailId : allEmails) {
            EmailRequest req = toSend.get(emailId);
            EmailDocument emailDocument;

            EmailDocument existingDoc = existingDocs.get(emailId);
            if(existingDoc != null) {
                if (!isEligibleToSend(existingDoc)) {
                    skippedEmails.add(emailId);
                    continue;
                }
                emailDocument = existingDoc;
                if(StringUtils.isNotBlank(req.getName())) emailDocument.setRecipientName(req.getName());
                if(StringUtils.isNotBlank(emailId)) emailDocument.setEmailTo(emailId);
                if(StringUtils.isNotBlank(req.getCompany())) emailDocument.setCompany(req.getCompany());
                Map<String, String> templateVariable = Map.of(
                        "name", emailDocument.getRecipientName(),
                        "company", emailDocument.getCompany()
                );
                emailDocument.setEmailTemplate(prepareEmailTemplate(templateVariable));
                emailDocument.setTemplateVariables(templateVariable);
            } else {
                emailDocument = prepareEmailDocument(
                        req.getName(),
                        req.getCompany(),
                        emailId
                );
            }
            try {
                asyncEmailSendService.sendEmail(emailDocument);
                Thread.sleep(2500);
            } catch (IOException | InterruptedException e) {
                log.error("Failed to send email to: {}", emailId, e);
                throw new RuntimeException(e);
            }
        }
        log.info("{} Emails skipped=[{}]", skippedEmails.size(), skippedEmails);
    }

    private Map<String, EmailRequest> getEmailRequestsFromCSV(MultipartFile file) {

        try (Reader reader = getReaderFromMultipart(file);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            Map<String, EmailRequest> toSend = new HashMap<>();
            for (CSVRecord record : csvParser) {
                try {
                    String emailId = record.get("email");
                    String name = record.get("name");
                    String company = record.get("company");
                    if(validateEmailFormat(emailId)) {
                        toSend.put(emailId, new EmailRequest(name, emailId, company));
                    } else {
                        log.warn("Email: {} not valid so skipped", emailId);
                    }
                } catch (Exception e) {
                    log.error("error while reading", e);
                }
            }
            return toSend;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Reader getReaderFromMultipart(MultipartFile file) throws IOException {

        if (file != null && !file.isEmpty()) {
            return new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
        }

        // Fallback to classpath resource
        Path csvPath = Paths.get("src/main/resources/data/contacts.csv");
        if (!Files.exists(csvPath)) {
            throw new FileNotFoundException("contacts.csv not found at " + csvPath.toAbsolutePath());
        }

        return Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
    }


    private boolean isEligibleToSend(EmailDocument emailDocument) {
        if (emailDocument == null || !emailDocument.isValidEmail()) return false;

        long cutoffTime = Instant.now().minus(HOURS_TO, ChronoUnit.HOURS).toEpochMilli();

        return !"DISABLED".equalsIgnoreCase(emailDocument.getStatus()) &&
                (emailDocument.getLastSentAt() <= cutoffTime || !"FAILED".equalsIgnoreCase(emailDocument.getStatus())) &&
                emailDocument.isResendEligible() &&
                emailDocument.getRetryCount() <= 15;
    }

    public String sendEligibleEmailsFromElastic(int hours) throws IOException {
//        hours = HOURS_TO; // comment if require from user input.
        List<EmailDocument> emailDocuments = getEligibleEmailDocuments(hours);
        if(emailDocuments.isEmpty()) {
            log.warn("No eligible email document found to send to ({} hours)", hours);
            return String.format("No eligible emails found up to %d", hours);
        }

        log.info("Sending {} eligible emails asynchronously...", emailDocuments.size());

        for (EmailDocument email : emailDocuments) {
            try {
                // @Async non-blocking parallel send
                asyncEmailSendService.sendEmail(email);
                Thread.sleep(2000);
            } catch (Exception e) {
                log.error("Failed to trigger email send for {}: {}", email.getEmailTo(), e.getMessage(), e);
            }
        }

        log.info("All eligible emails submitted for sending.");
        return String.format("Submitted %d emails for sending", emailDocuments.size());
    }

    public List<EmailDocument> getEligibleEmailDocuments(int hours) throws IOException {
//        hours = DUPLICATE_DAYS; // comment if require custom days.
        Instant instant = Instant.now();
        long lte = instant.minus(0, ChronoUnit.HOURS).toEpochMilli();
        long gte = instant.minus(hours + 120, ChronoUnit.HOURS).toEpochMilli();
        return emailElasticSyncService.getEligibleEmails(gte, lte);
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
        emailElasticSyncService.bulkIndexEmails(emailDocuments);

        return emailDocuments;
    }

    public long getEligibleEmailsCount(int hours) throws IOException {
        Instant instant = Instant.now();
        long lte = instant.minus(hours, ChronoUnit.HOURS).toEpochMilli();
        long gte = instant.minus(hours + 120, ChronoUnit.HOURS).toEpochMilli();
        return emailElasticSyncService.getEligibleEmailsCount(gte, lte);
    }

    private Map<String, EmailDocument> prepareEmailDocumentsFromRawInput(String rawInput) {
        return Arrays.stream(rawInput.split("\\r?\\n"))
                .skip(1)
                .map((emailRow)-> {

                    String[] parts = emailRow.split(",");
                    String emailTo = parts[1].trim().toLowerCase();
                    if (parts.length >= 3 && !validateEmailFormat(emailTo)) {
                        log.warn("Invalid line skipped: {}", emailRow);
                        return null;
                    }

                    String name = parts[0].trim();
                    String company = parts[2].trim();

                    return prepareEmailDocument(name, company, emailTo);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        EmailDocument::getEmailTo,
                        Function.identity(),
                        (existing, replacement)-> existing
                ));
    }

    private EmailDocument prepareEmailDocument(String recipientName, String companyName, String emailTo) {
        // --- Initialize document ---
        EmailDocument emailDocument = new EmailDocument();
        emailDocument.setEmailTo(emailTo);
        emailDocument.setRecipientName(recipientName);
        emailDocument.setCompany(companyName);
        emailDocument.setEmailFrom("mrsinghvinay563@gmail.com");
        emailDocument.setSubject("Java Full Stack Developer Application For New Opportunities");

        // --- Template & content ---
        emailDocument.setHtml(true);
        emailDocument.setTemplateName("email-template.html");
        Map<String, String> templateVariables = new HashMap<>();
        templateVariables.put("name", recipientName);
        templateVariables.put("company", companyName);
        emailDocument.setTemplateVariables(templateVariables);
        emailDocument.setEmailTemplate(prepareEmailTemplate(templateVariables));

        // --- Attachments ---
        emailDocument.setAttachmentNames(
                new ArrayList<>(List.of("Vinay_Singh_Java_Backend_Developer.pdf"))
        );
        emailDocument.setAttachmentSizeBytes(0L); // You can populate later if you measure actual file size

        // --- Status & tracking ---
        emailDocument.setStatus("READY");
        emailDocument.setCategory("APPLICATION");
        emailDocument.setResendEligible(true);
        emailDocument.setValidEmail(validateEmailFormat(emailTo));
        emailDocument.setRetryCount(0);
        emailDocument.setEmailSentTimes(0);
//        emailDocument.setLastUpdatedAt(Instant.now().toEpochMilli());
        emailDocument.setLastSentAt(0L); // Not sent yet
        emailDocument.setDeliveryTimeMs(0L);

        // --- System info ---
        emailDocument.setEnvironment("development");
        emailDocument.setSentBy("mrsinghvinay563@gmail.com");
        emailDocument.setThreadName(Thread.currentThread().getName());

        Instant randomInstant = Instant.now();

        // Just for testing only to simulate ideal scenario
//        boolean flag = true;
//        Random random = new Random();
//        Instant instantNow = Instant.now();
//        randomInstant = Instant.now().minus(DUPLICATE_DAYS, ChronoUnit.DAYS);
//        if(flag) {
//            flag = false;
//            long moreDaysAgo = DUPLICATE_DAYS + random.nextInt(20);
//            randomInstant = instantNow.minus(moreDaysAgo, ChronoUnit.DAYS);
//        } else {
//            flag = true;
//            long lessDaysAgo = DUPLICATE_DAYS - random.nextInt(20);
//            randomInstant = instantNow.minus(lessDaysAgo, ChronoUnit.DAYS);
//        }

        emailDocument.setLastUpdatedAt(randomInstant.toEpochMilli());
        emailDocument.setLastSentAt(randomInstant.toEpochMilli());
        emailDocument.setDateCreated(randomInstant);

        return emailDocument;
    }

    private boolean validateEmailFormat(String email) {
        Pattern EMAIL_PATTERN = Pattern.compile(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public List<EmailDocument> getEmailDocumentFromEmailIds(List<String> emailIds) throws IOException {

        return emailElasticSyncService.getEmailDocumentFromEmailIds(emailIds);
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

}
