package com.dev.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PdfEmailExtractorService {

    /**
     * A regex pattern to detect valid email addresses.
     * This pattern is based on RFC 5322 simplified rules.
     *
     * Example matches:
     * - user@example.com
     * - john.doe+test@company.co.in
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z0-9.\\-]{2,}"
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
                    emails.add(matcher.group());
                }

            } catch (IOException e) {
                log.error("Exception: ", e);
                // Wrap the exception into an unchecked one to simplify API layer handling
                throw new RuntimeException("Failed to extract emails from PDF", e);
            }
        }
        return emails;
    }
}
