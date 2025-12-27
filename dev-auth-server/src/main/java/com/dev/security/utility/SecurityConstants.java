package com.dev.security.utility;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

public class SecurityConstants {

    @Value("${dev.auth.sign_secret_key:vyubjnvf9876ftvbefudnv78ref6g7-ewrfdvbhj-ewrfdhbn2134wer==}")
    public static final String SIGNING_SECRET_KEY = "vyubjnvf9876ftvbefudnv78ref6g7-ewrfdvbhj-ewrfdhbn2134wer==";

    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC_AUTH = "Basic";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static final String ORGANIZATION = "organization";
    public static final String TENANT = "tenant";

    public static final String ISSUER = "dev-auth-server";
    public static final List<String> AUDIENCE = Arrays.asList("dev-sandbox", "dev-integration", "dev-auth-server");
    public static final List<String> AUTHZ = Arrays.asList("ADMIN", "USER", "MANAGER");
    public static final String PERMISSION = "permission";
    public static final String AUTH_TYPE = "type";
    public static final int MAX_TOKEN_SIZE = 4096;
    public static final long CLOCK_SKEW_SECONDS = 30;
}
