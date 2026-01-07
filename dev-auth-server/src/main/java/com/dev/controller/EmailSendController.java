package com.dev.controller;

import com.dev.library.elastic.service.EmailElasticSyncService;
import com.dev.service.impl.EmailPrepareService;
import com.dev.dto.email.EmailDocument;
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
public class EmailSendController {
    private final EmailPrepareService emailPrepareService;
    private final EmailElasticSyncService emailElasticSyncService;

    @GetMapping(value = "/send-from-csv-file-v2")
    public void sendEmailsFromFile() throws Exception {
        emailPrepareService.sendEmailFromCSVFile(null);
    }

    @PostMapping(value = "/send-from-csv-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmailsFromFile(@RequestParam("file") MultipartFile file) throws IOException {
        emailPrepareService.sendEmailFromCSVFile(file);
        return new ResponseEntity<>("Email sent", HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/sync")
    public ResponseEntity<Map<String, EmailDocument>> syncEmailsToES(@RequestBody String rawInput) throws IOException {
        Map<String, EmailDocument> processed = emailPrepareService.processAndSyncEmails(rawInput);
        return ResponseEntity.ok(processed);
    }

    @PostMapping(value = "/eligible-emails")
    public ResponseEntity<List<EmailDocument>> getEligibleEmailDocuments(@RequestParam(value = "hours", defaultValue = "12", required = false) int hours) throws IOException {
        List<EmailDocument> processed = emailPrepareService.getEligibleEmailDocuments(hours);
        return ResponseEntity.ok(processed);
    }

    @PostMapping(value = "/send-from-elastic")
    public ResponseEntity<String> setEmailsFromElasticsearch(@RequestParam(value = "hours", defaultValue = "12", required = false) int hours) throws IOException {
        return ResponseEntity.ok(emailPrepareService.sendEligibleEmailsFromElastic(hours));
    }

    @GetMapping(value = "/eligible-email-count")
    public ResponseEntity<Long> getEligibleEmailsCount(@RequestParam(value = "hours", defaultValue = "12", required = false) int hours) throws Exception {
        return ResponseEntity.ok(emailPrepareService.getEligibleEmailsCount(hours));
    }

    @PostMapping(value = "/email-docs-by-ids")
    public ResponseEntity<List<EmailDocument>> getEmailDocumentFromEmailIds(@RequestBody List<String> emailIds) throws IOException {
        return ResponseEntity.ok(emailPrepareService.getEmailDocumentFromEmailIds(emailIds));
    }

    @GetMapping(value = "/create-index/{index}")
    public ResponseEntity<String> createEmailIndex(@PathVariable String index) throws IOException {
        return ResponseEntity.ok(emailElasticSyncService.createIndexWithAliasAdnMapping(index));
    }

    @GetMapping(value = "/reindex/{aliasIndex}")
    public ResponseEntity<String> reindexTenantIndex(@PathVariable String aliasIndex) throws IOException {
        return ResponseEntity.ok(emailElasticSyncService.reindexTenantIndex(aliasIndex));
    }
}
