package com.dev.bulk.email.service;

import com.dev.bulk.email.dto.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async("threadPoolTaskExecutor")
    public void sendEmail(EmailRequest request, File resumeFile) throws MessagingException {
        log.info("Preparing email template for: {}, thread: {}", request.getEmailTo(), Thread.currentThread().getName());
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(request.getEmailTo());
        helper.setSubject(request.getSubject());
        helper.setFrom(request.getEmailFrom());

        Context context = new Context();
        context.setVariable("name", request.getName());
        context.setVariable("company", request.getCompany());

        String htmlContent = templateEngine.process("email-template.html", context);
        helper.setText(htmlContent, true);

        helper.addAttachment("Vinay_Java_Backend_Developer.pdf", resumeFile);
        log.info("Sending email to: {}, company: {}", request.getEmailTo(), request.getCompany());
        mailSender.send(message);
        log.info("Email sent to: {}", request.getEmailTo());
    }
}
