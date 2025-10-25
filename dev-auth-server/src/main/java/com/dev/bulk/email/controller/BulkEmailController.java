package com.dev.bulk.email.controller;

import com.dev.bulk.email.dto.EmailRequest;
import com.dev.bulk.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

@RestController
@RequestMapping(value = "/mail")
@RequiredArgsConstructor
@Slf4j
public class BulkEmailController {
    private final EmailService emailService;

    @GetMapping(value = "/bulk")
    public void run() throws Exception {
        File csvFile = new File("/home/guyvinay/dev/repo/assets/hr_contacts.csv");
        File resume = new File("/home/guyvinay/dev/repo/assets/Vinay_Singh_Java_Backend_Developer.pdf");

        try (Reader reader = new FileReader(csvFile);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                EmailRequest request = new EmailRequest(
                        record.get("name"),
                        record.get("email"),
                        "mrsinghvinay563@gmail.com",
                        record.get("company"),
                        "Java Backend Developer Application For New Opportunities"
                );

                try {
                    emailService.sendEmail(request, resume);
                } catch (Exception e) {
                    System.err.println("Failed to send email to: " + record.get("email"));
                    e.printStackTrace();
                }
            }
        }
    }
}
