package com.dev.service.impl;

import com.dev.dto.email.EmailCategory;
import com.dev.dto.email.EmailDocument;
import com.dev.dto.email.EmailRequest;
import com.dev.library.elastic.service.EmailElasticSyncService;
import com.dev.utility.grpc.email.*;
import com.dev.utils.EmailEventMapper;
import com.dev.utility.PdfEmailExtractorService;
import com.dev.utils.GrpcMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;
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
    private static final int HOURS_TO = 480;
    private final AsyncEmailSendService asyncEmailSendService;
    private final PdfEmailExtractorService pdfEmailExtractorService;
    private final EsEmailDocumentService esEmailDocumentService;

    @GrpcClient(DEV_INTEGRATION)
    private EmailElasticServiceGrpc.EmailElasticServiceBlockingStub elasticServiceStub;

    public void sendEmailFromCSVFile(MultipartFile file) throws IOException {
        List<String> skippedEmails = new ArrayList<>();
        Map<String, EmailRequest> toSend = getEmailRequestsFromCSV(file);
        List<String> allEmails = new ArrayList<>(toSend.keySet());
        List<EmailDocument> docsToSave = new ArrayList<>();
        List<EmailDocument> allEmailDocuments = new ArrayList<>();
        log.info("Total emails from CSV: {}", allEmails.size());
        Map<String, EmailDocument> existingDocs = esEmailDocumentService.fetchExistingDocuments(allEmails);

        log.info("Existing doc found: {}", existingDocs.size());

        for (String emailId : allEmails) {
            EmailRequest req = toSend.get(emailId);
            EmailDocument emailDocument = existingDocs.get(emailId);
            if(emailDocument != null) {
                if (!isEligibleToSend(emailDocument)) {
                    skippedEmails.add(emailId);
                    continue;
                }
                enrichExistingDocument(emailDocument, req);
                emailDocument.setEmailSentTimes(emailDocument.getEmailSentTimes() + 1);
            } else {
                emailDocument = prepareEmailDocument(
                        req.getName(),
                        req.getCompany(),
                        emailId
                );
                docsToSave.add(emailDocument);
            }
            allEmailDocuments.add(emailDocument);
        }

        log.info("Total docs to save: {}", docsToSave.size());
        List<String> failedToIndex =
                esEmailDocumentService.bulkIndexNewDocuments(docsToSave);

        if (!failedToIndex.isEmpty()) {
            log.info("Documents failed to save count={}", failedToIndex.size());
            Set<String> failedSet = new HashSet<>(failedToIndex);
            allEmailDocuments.removeIf(doc ->
                    failedSet.contains(doc.getEmailTo()));
        }

        log.info("Total emails to send={}", allEmailDocuments.size());

        for (EmailDocument emailDocument : allEmailDocuments) {
            asyncEmailSendService.sendRmqEmailEvent(
                    EmailEventMapper.toSendEventFromEmailDocuments(emailDocument)
            );
        }

        log.info("{} Emails skipped=[{}]", skippedEmails.size(), skippedEmails);
    }

    private void enrichExistingDocument(EmailDocument doc, EmailRequest req) {

        if (StringUtils.isNotBlank(req.getName()))
            doc.setRecipientName(req.getName());

        if (StringUtils.isNotBlank(req.getCompany()))
            doc.setCompany(req.getCompany());

        Map<String, String> templateVariables = Map.of(
                "name", doc.getRecipientName(),
                "company", doc.getCompany()
        );

        doc.setTemplateVariables(templateVariables);
        doc.setEmailTemplate(prepareEmailTemplate(templateVariables));
        doc.setAttachmentNames(
                new ArrayList<>(List.of("Vinay_Singh_Java_Backend_Developer.pdf"))
        );
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
                (emailDocument.getLastSentAt() <= cutoffTime) &&
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

        for (EmailDocument emailDocument : emailDocuments) {
            try {
                EmailRequest emailRequest = pdfEmailExtractorService.processEmailToGetNameAndCompany(emailDocument.getEmailTo());
                enrichExistingDocument(emailDocument, emailRequest);
                // @Async non-blocking parallel send
                asyncEmailSendService.sendEmail(emailDocument);
                Thread.sleep(2000);
            } catch (Exception e) {
                log.error("Failed to trigger email send for {}: {}", emailDocument.getEmailTo(), e.getMessage(), e);
            }
        }

        log.info("All eligible emails submitted for sending.");
        return String.format("Submitted %d emails for sending", emailDocuments.size());
    }

    public List<EmailDocument> getEligibleEmailDocuments(int hours) throws IOException {
//        hours = DUPLICATE_DAYS; // comment if require custom days.
        Instant instant = Instant.now();
        long lte = instant.minus(hours, ChronoUnit.HOURS).toEpochMilli();
        long gte = instant.minus(hours + 72, ChronoUnit.HOURS).toEpochMilli();
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
        emailDocument.setSubject("Java Full Stack Developer Application Immediate joiner");

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
        emailDocument.setCategory(EmailCategory.APPLICATION);
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
