package com.dev.bulk.email.controller;

import com.dev.bulk.email.dto.EmailDocument;
import com.dev.bulk.email.service.EmailElasticService;
import com.dev.bulk.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/emails")
@RequiredArgsConstructor
@Slf4j
public class BulkEmailController {
    private final EmailService emailService;
    private final EmailElasticService emailElasticService;

    @GetMapping(value = "/send-from-csv-file-v2")
    public void sendEmailsFromFile() throws Exception {
        emailService.sendEmailFromCSVFile(null);
    }

    @PostMapping(value = "/send-from-csv-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmailsFromFile(@RequestParam("file") MultipartFile file) throws IOException {
        emailService.sendEmailFromCSVFile(file);
        return new ResponseEntity<>("Email sent", HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/sync")
    public ResponseEntity<Map<String, EmailDocument>> syncEmailsToES(@RequestBody String rawInput) throws IOException {
        Map<String, EmailDocument> processed = emailService.processAndSyncEmails(rawInput);
        return ResponseEntity.ok(processed);
    }

    @PostMapping(value = "/eligible-emails")
    public ResponseEntity<List<EmailDocument>> getEligibleEmailDocuments(@RequestParam(value = "hours", defaultValue = "12", required = false) int hours) throws IOException {
        List<EmailDocument> processed = emailService.getEligibleEmailDocuments(hours);
        return ResponseEntity.ok(processed);
    }

    @PostMapping(value = "/send-from-elastic")
    public ResponseEntity<String> setEmailsFromElasticsearch(@RequestParam(value = "hours", defaultValue = "12", required = false) int hours) throws IOException {
        return ResponseEntity.ok(emailService.sendEligibleEmailsFromElastic(hours));
    }

    @GetMapping(value = "/eligible-email-count")
    public ResponseEntity<Long> getEligibleEmailsCount(@RequestParam(value = "hours", defaultValue = "12", required = false) int hours) throws Exception {
        return ResponseEntity.ok(emailService.getEligibleEmailsCount(hours));
    }

    @PostMapping(value = "/email-docs-by-ids")
    public ResponseEntity<List<EmailDocument>> getEmailDocumentFromEmailIds(@RequestBody List<String> emailIds) throws IOException {
        return ResponseEntity.ok(emailService.getEmailDocumentFromEmailIds(emailIds));
    }

    @GetMapping(value = "/create-index/{index}")
    public ResponseEntity<String> createEmailIndex(@PathVariable String index) throws IOException {
        return ResponseEntity.ok(emailElasticService.createIndexWithAliasAdnMapping(index));
    }

    @GetMapping(value = "/reindex/{aliasIndex}")
    public ResponseEntity<String> reindexTenantIndex(@PathVariable String aliasIndex) throws IOException {
        return ResponseEntity.ok(emailElasticService.reindexTenantIndex(aliasIndex));
    }
}
