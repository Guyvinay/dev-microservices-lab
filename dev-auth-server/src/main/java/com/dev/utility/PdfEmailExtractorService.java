package com.dev.utility;

import com.dev.dto.email.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class PdfEmailExtractorService {

    public static final Set<String> ROLE_PREFIXES = Set.of(

            // HR / Recruitment
            "hr", "hrd", "hrteam", "hrdesk", "hrsupport",
            "recruit", "recruiter", "recruitment", "recruiting",
            "talent", "talentacquisition", "hiring",
            "staffing", "placements", "placement",
            "careers", "career", "jobs", "job", "jobportal",
            "vacancy", "vacancies", "openings", "opportunity",

            // Generic Corporate
            "info", "support", "help", "helpdesk",
            "contact", "connect", "enquiry", "inquiry",
            "admin", "administrator", "office", "frontdesk",
            "team", "staff", "group",

            // Automation / System
            "noreply", "no-reply", "mailer", "mail", "automail",
            "auto", "system", "daemon", "robot", "bot",
            "notifications", "alerts",

            // Applications
            "apply", "applications", "application",
            "submissions", "submission",

            // Sales / Marketing (not real persons)
            "sales", "marketing", "business", "bd", "partnership",
            "alliances", "growth","resume",

            // Finance / Ops
            "accounts", "billing", "finance", "payroll",
            "operations", "ops",

            // Generic Aliases
            "hello", "welcome", "feedback",
            "care", "customersupport", "customerservice",

            // Regional Mailboxes
            "india", "us", "usa", "uk", "europe", "apac", "emea",

            // Other common garbage prefixes
            "test", "demo", "sample", "example",
            "agency", "agencies", "agen","tag"
    );


    public static final Set<String> PUBLIC_PROVIDERS = Set.of(

            // Global
            "gmail", "outlook", "hotmail", "yahoo", "live", "icloud",
            "protonmail", "aol", "mail", "gmx",

            // India
            "rediff", "yandex",

            // Business-style public mail
            "zoho", "zohomail", "yahooo",

            // Temporary / spam prone
            "tempmail", "10minutemail", "mailinator", "guerrillamail",

            // ISP mailboxes
            "comcast", "verizon", "att", "sbcglobal", "btinternet",

            // Old providers
            "inbox", "fastmail", "rocketmail","recruit",
            "info", "support", "help", "helpdesk","careers", "career", "jobs", "job", "jobportal",
            "vacancy", "vacancies", "openings", "opportunity","tag"
    );


    private static final String CSV_HEADER = "name,email,company";

    private static final Path CSV_PATH =
            Paths.get("src/main/resources/data/contacts.csv");


    /**
     * A regex pattern to detect valid email addresses.
     * This pattern is based on RFC 5322 simplified rules.
     *
     * Example matches:
     * - user@example.com
     * - john.doe+test@company.co.in
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "\\b[a-zA-Z0-9][a-zA-Z0-9._%+\\-]*@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\\b"
    );


    /**
     * Extract all email addresses from a PDF file.
     *
     * @return A Set<String> of unique email addresses found in the PDF
     */
    public Set<String> extractEmailsFromPdfFiles(List<MultipartFile> pdfFiles) throws IOException {

        // Using Set to avoid duplicate emails automatically
        Set<String> emails = new HashSet<>();
        for (MultipartFile file: pdfFiles) {
            String fileName = file.getOriginalFilename();
            Set<String> currentEmails = new HashSet<>();
            log.info("Extracting email start from {}", fileName);
            InputStream pdfStream = file.getInputStream();

            /**
             * PDDocument.load() loads the PDF structure into memory.
             * try-with-resources ensures the document is closed automatically.
             */
            try (PDDocument document = PDDocument.load(pdfStream)) {

                /**
                 * PDFTextStripper is a utility class that extracts all text from the PDF.
                 * It reads every page of the PDF and returns the content as plain text.
                 */
                PDFTextStripper stripper = new PDFTextStripper();

                // Extract the text content from the entire PDF
                String text = stripper.getText(document);

                /**
                 * Match the extracted text against the EMAIL_PATTERN regex.
                 * matcher.find() iterates through every match inside the text.
                 */
                Matcher matcher = EMAIL_PATTERN.matcher(text);
                while (matcher.find()) {
                    // matcher.group() returns the exact email that matched the regex
                    String email = matcher.group();

                    // Remove trailing punctuation like .,;:
                    email = email.replaceAll("[\\.,;:]+$", "").trim();

                    currentEmails.add(email);
                }
                log.info("Extracted {} emails from {}", currentEmails.size(), fileName);
                emails.addAll(currentEmails);
            } catch (IOException e) {
                log.error("Exception: ", e);
                // Wrap the exception into an unchecked one to simplify API layer handling
                throw new RuntimeException("Failed to extract emails from PDF", e);
            }
        }

        log.info("Total {} emails extracted",  emails.size());
        writeContacts(emails);
        return emails;
    }
    public Set<String> extractEmailsFromHtmlFiles(List<MultipartFile> htmlFiles) throws IOException {

        Set<String> emails = new HashSet<>();

        for (MultipartFile file : htmlFiles) {

            String fileName = file.getOriginalFilename();
            Set<String> currentEmails = new HashSet<>();

            log.info("Extracting emails from HTML file {}", fileName);

            try (InputStream inputStream = file.getInputStream()) {

                /**
                 * Jsoup parses the HTML safely.
                 * It tolerates broken HTML and avoids regex-based parsing issues.
                 */
                Document document = Jsoup.parse(inputStream, StandardCharsets.UTF_8.name(), "");

                /**
                 * Remove script and style content to reduce noise
                 * and avoid extracting emails from JavaScript.
                 */
                document.select("script, style").remove();

                /**
                 * Extract visible text only.
                 */
                String text = document.text();

                Matcher matcher = EMAIL_PATTERN.matcher(text);

                while (matcher.find()) {
                    String email = matcher.group();
                    email = email.replaceAll("[\\.,;:]+$", "").trim();
                    currentEmails.add(email);
                }

                log.info("Extracted {} emails from {}", currentEmails.size(), fileName);
                emails.addAll(currentEmails);

            } catch (IOException e) {
                log.error("Error extracting emails from {}", fileName, e);
                throw new RuntimeException("Failed to extract emails from HTML", e);
            }
        }

        log.info("Total {} emails extracted", emails.size());
        writeContacts(emails);

        return emails;
    }

    public EmailRequest processEmailToGetNameAndCompany(String email) {

        try {
            String trimmed = email.trim();

            String[] parts = trimmed.split("@");

            String local = parts[0];
            String domain = parts[1];

            String company = extractPrimaryDomain(domain);

            String name = extractName(local);

            return new EmailRequest(name, trimmed, company);
        } catch (Exception e) {
            log.error("Error while converting email to EmailRequest: {}", e.getMessage(), e);
            return null;
        }
    }

    public static String extractPrimaryDomain(String domain) {

        if (domain == null || domain.isBlank()) {
            return "your organization";
        }

        String cleaned = domain
                .toLowerCase()
                .trim()
                .replaceAll("\\.$", "");

        String[] parts = cleaned.split("\\.");

        // Example: gmail.com
        if (parts.length == 2) {

            String base = parts[0];

            if (PUBLIC_PROVIDERS.contains(base)) {
                return "your organization";
            }

            return capitalize(base);
        }

        // Example: something.co.in | something.com.au
        if (parts.length >= 3) {

            String tld = parts[parts.length - 1];
            String sld = parts[parts.length - 2];

            // Handle multi-level TLDs (co.in, co.uk, com.au, org.in, net.in)
            if (tld.length() == 2 && sld.length() <= 3) {

                String base = parts[parts.length - 3];

                if (PUBLIC_PROVIDERS.contains(base)) {
                    return "your organization";
                }

                return capitalize(base);
            }

            // Normal domain (company.com)
            String base = sld;

            if (PUBLIC_PROVIDERS.contains(base)) {
                return "your organization";
            }

            return capitalize(base);
        }

        return "your organization";
    }

    public static String extractName(String localPart) {

        if (localPart == null || localPart.isBlank()) {
            return "HR";
        }

        String prefix = localPart.trim().toLowerCase();

        // Allow only safe characters
        if (!prefix.matches("[A-Za-z0-9._-]+")) {
            return "HR";
        }

        // Block role-based addresses completely
        for (String role : ROLE_PREFIXES) {
            if (prefix.startsWith(role) || prefix.contains(role)) {
                return "HR";
            }
        }

        // Split by common separators
        String[] tokens = prefix.split("[._-]");

        for (String token : tokens) {

            // Remove digits from token
            String cleaned = token.replaceAll("\\d", "");

            // Skip empty or single-letter tokens (like "s" in aftab.s)
            if (cleaned.length() < 3) {
                continue;
            }

            // Only alphabetic allowed
            if (!cleaned.matches("[a-z]+")) {
                continue;
            }

            // Length sanity check
            if (cleaned.length() > 15) {
                continue;
            }

            return capitalize(cleaned);
        }

        // Fallback
        return "HR";
    }


    private static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


    public void writeContacts(Set<String> emails) throws IOException {

        Files.createDirectories(CSV_PATH.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(
                CSV_PATH,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        )) {

            writer.write(CSV_HEADER);
            writer.newLine();

            for (String email : emails) {

                try {
                    EmailRequest row = processEmailToGetNameAndCompany(email);
                    if(row != null) {
                        writer.write(formatRow(row));
                        writer.newLine();
                    }
                } catch (IOException e) {
                    log.error("Erro while writing email to csv file");
                }
            }
        }
    }

    private String formatRow(EmailRequest row) {

        return row.getName() + ","
                + row.getEmailTo() + ","
                + row.getCompany();
    }
}
