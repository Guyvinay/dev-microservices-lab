package com.dev.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.mail")
public class CustomMailProperties {

    /**
     * SMTP server hostname.
     *
     * Example:
     * smtp.gmail.com
     * email-smtp.ap-south-1.amazonaws.com (AWS SES)
     */
    private String host;

    /**
     * SMTP server port.
     *
     * Common values:
     * 587 → STARTTLS (recommended, secure)
     * 465 → SSL (implicit TLS)
     * 25  → Plain SMTP (NOT recommended for production)
     */
    private int port;

    /**
     * Username for SMTP authentication.
     *
     * Usually:
     * - Email address (Gmail, Outlook)
     * - SMTP IAM user (AWS SES)
     */
    private String username;

    /**
     * Password or SMTP credential.
     *
     * ⚠ Always load from environment variables or secrets.
     * Never hardcode in source code.
     */
    private String password;

    /**
     * Mail transport protocol.
     *
     * Default: smtp
     * Rarely changed unless using custom mail transports.
     */
    private String protocol = "smtp";

    /**
     * Enables SMTP authentication.
     *
     * Must be TRUE for most production mail servers.
     * Prevents anonymous mail relaying.
     */
    private boolean auth;

    /**
     * Enables STARTTLS encryption upgrade.
     *
     * If true:
     * Connection starts as plain text and is upgraded to TLS.
     *
     * This protects credentials and email content in transit.
     */
    private boolean startTlsEnable;

    /**
     * Forces TLS usage.
     *
     * If true:
     * Connection fails if TLS cannot be established.
     *
     * Recommended for production to avoid insecure fallback.
     */
    private boolean startTlsRequired;

    /**
     * Maximum time (ms) to wait while establishing SMTP connection.
     *
     * Prevents threads from hanging when SMTP server is unreachable.
     *
     * Recommended: 3000–5000 ms
     */
    private int connectionTimeout;

    /**
     * Maximum time (ms) to wait for server response.
     *
     * Applies after connection is established.
     *
     * Protects against slow or stuck mail servers.
     */
    private int timeout;

    /**
     * Maximum time (ms) allowed for sending email data.
     *
     * Important when sending large emails or attachments.
     *
     * Prevents infinite write blocking.
     */
    private int writeTimeout;

    /**
     * Enables SMTP connection pooling.
     *
     * When true:
     * Reuses SMTP connections instead of opening new one per email.
     *
     * This massively improves throughput and reduces latency.
     *
     * ⚠ MUST be enabled for high volume email systems.
     */
    private boolean poolEnabled;

    /**
     * Maximum number of SMTP connections in pool.
     *
     * Recommended sizing:
     * poolSize ≈ number of concurrent consumers/threads
     *
     * Example:
     * 5 pods × 2 consumers = 10 pool size
     */
    private int poolSize;

    /**
     * Default character encoding used for email content.
     *
     * UTF-8 is recommended to support:
     * - International characters
     * - Emojis
     * - Multilingual templates
     */
    private String defaultEncoding = "UTF-8";
}
