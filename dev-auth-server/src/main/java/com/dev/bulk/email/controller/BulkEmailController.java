package com.dev.bulk.email.controller;

import com.dev.bulk.email.dto.EmailDocument;
import com.dev.bulk.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/emails")
@RequiredArgsConstructor
@Slf4j
public class BulkEmailController {
    private final EmailService emailService;

    @GetMapping(value = "/send-from-csv-file")
    public void run() throws Exception {
        emailService.sendEmailFromCSVFile();
    }

    @PostMapping(value = "/sync")
    public ResponseEntity<Map<String, EmailDocument>> syncEmailsToES(@RequestBody String rawInput) throws IOException {
        Map<String, EmailDocument> processed = emailService.processAndSyncEmails(rawInput);
        return ResponseEntity.ok(processed);
    }

    @PostMapping(value = "/eligible-emails")
    public ResponseEntity<List<EmailDocument>> getEligibleEmailDocuments(@RequestParam(value = "days", defaultValue = "5", required = false) int daysAgo) throws IOException {
        List<EmailDocument> processed = emailService.getEligibleEmailDocuments(daysAgo);
        return ResponseEntity.ok(processed);
    }

    @PostMapping(value = "/send-from-elastic")
    public ResponseEntity<String> setEmailsFromElasticsearch(@RequestParam(value = "days", defaultValue = "5", required = false) int daysAgo) throws IOException {
        return ResponseEntity.ok(emailService.sendEligibleEmailsFromElastic(daysAgo));
    }


}
