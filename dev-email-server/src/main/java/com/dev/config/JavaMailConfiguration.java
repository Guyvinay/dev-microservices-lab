package com.dev.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class JavaMailConfiguration {

    @Bean
    public JavaMailSender javaMailSender(CustomMailProperties  properties) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();

        sender.setHost(properties.getHost());
        sender.setPort(properties.getPort());
        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setProtocol(properties.getProtocol());

        Properties props = getProperties(properties);

        sender.setJavaMailProperties(props);
        sender.setDefaultEncoding("UTF-8");

        return sender;
    }

    private static @NonNull Properties getProperties(CustomMailProperties properties) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", properties.isAuth());
        props.put("mail.smtp.starttls.enable", properties.isStartTlsEnable());
        props.put("mail.smtp.starttls.required", properties.isStartTlsRequired());

        // Timeouts (IMPORTANT)
        props.put("mail.smtp.connectiontimeout", properties.getConnectionTimeout());
        props.put("mail.smtp.timeout", properties.getTimeout());
        props.put("mail.smtp.writetimeout", properties.getWriteTimeout());

        // Connection Pooling
        props.put("mail.smtp.pool", properties.isPoolEnabled());
        props.put("mail.smtp.pool.size", properties.getPoolSize());
        return props;
    }

}
