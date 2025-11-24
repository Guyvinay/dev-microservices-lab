package com.dev.controller;

import com.dev.utility.PdfEmailExtractorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfEmailExtractorService pdfEmailExtractorService;

    public PdfController(PdfEmailExtractorService pdfEmailExtractorService) {
        this.pdfEmailExtractorService = pdfEmailExtractorService;
    }

    /**
     * Endpoint: POST /api/pdf/extract-emails
     *
     * Accepts a PDF file uploaded via Multipart/form-data
     * Example request using Postman:
     * - Key: file
     * - Type: File
     */
    @PostMapping(value = "/extract-emails", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Set<String>> extractEmails(@RequestParam("files") List<MultipartFile> pdfFiles) {
        try {
            Set<String> emails = pdfEmailExtractorService.extractEmailsFromPdfFiles(pdfFiles);
            return ResponseEntity.ok(emails);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.emptySet());
        }
    }
}
